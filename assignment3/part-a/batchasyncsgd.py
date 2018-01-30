import tensorflow as tf
import datetime
import os

dataPath_prefix = '/home/ubuntu/tf/TF_Scripts/uploaded-scripts/data/criteo-tfr'

# number of features in the criteo dataset after one-hot encoding
num_features = 33762578
num_test_examples = 10000
num_iterations = 10
learning_rate = .01
batch_size = 100

def getFilenames(machine_index):
    if (machine_index in [0, 1]) :
        return [dataPath_prefix + "/tfrecords0" + str(i) for i in range(machine_index*5, (machine_index+1)*5)]
    if (machine_index in [2, 3]) :
        return [dataPath_prefix + "/tfrecords" + str(i) for i in range(machine_index*5, (machine_index+1)*5)]
    return [dataPath_prefix + "/tfrecords2" + str(i) for i in range(0, 2)]

tf.app.flags.DEFINE_integer("task_index", 0, "Index of the worker task")
FLAGS = tf.app.flags.FLAGS

g = tf.Graph()

with g.as_default():
    # creating a model variable on task 0. This is a process running on node vm-11-1
    with tf.device("/job:worker/task:0"):
	w = tf.Variable(.1*tf.ones([num_features]), name="model")
	w_copy = tf.Variable(.1*tf.ones([num_features]), name="model_copy")

    local_gradient = {}
    with tf.device("/job:worker/task:%d" % FLAGS.task_index):
        local_gradient["local_grad_%d" % FLAGS.task_index] = tf.SparseTensor(indices=[[0]], values = [0.0], shape=[num_features])
        reader = tf.TFRecordReader(name="reader_%d" % FLAGS.task_index)
        filename_queue= tf.train.string_input_producer(getFilenames(FLAGS.task_index), num_epochs=None, name = "queue_%d" % FLAGS.task_index)
        _, serialized_examples = reader.read_up_to(filename_queue, batch_size, name = "serialized_example_%d" % FLAGS.task_index)

        features = tf.parse_example(serialized_examples,
                                   features={
                                    'label': tf.FixedLenFeature([1], dtype=tf.int64),
                                    'index' : tf.VarLenFeature(dtype=tf.int64),
                                    'value' : tf.VarLenFeature(dtype=tf.float32),
                                   }, name = "features_%d" % FLAGS.task_index
                                  )

        label_list = features['label']
        read_batch_size = batch_size
        index_list = tf.sparse_split(0, read_batch_size, features['index'])
        value_list = tf.sparse_split(0, read_batch_size, features['value'])
        
	for j in range(0, read_batch_size):
            label = tf.cast(label_list[j], tf.float32)
            index = index_list[j]
            value = value_list[j]
            
            with tf.device("/job:worker/task:0"):
                w_small = tf.gather(w, index.values)

            with tf.device("/job:worker/task:%d" % FLAGS.task_index):
                m = tf.mul(value.values, w_small)
                dot_product = tf.reduce_sum(m)
                sigmoid = tf.sigmoid(tf.mul(label, dot_product))
                local_gradient_val = tf.mul(value.values, (learning_rate/read_batch_size)*tf.mul(label, tf.sub(sigmoid, 1)))
                local_gradient["local_grad_%d" % FLAGS.task_index] = tf.sparse_add(
                        local_gradient["local_grad_%d" % FLAGS.task_index],
                        tf.SparseTensor(indices=tf.reshape(index.values, [-1, 1]), values = local_gradient_val, shape=[num_features]))
        
    # we create an operator to update the gradient
    with tf.device("/job:worker/task:0"):
        update_w = w.scatter_sub(tf.IndexedSlices(local_gradient["local_grad_%d" % FLAGS.task_index].values, tf.reshape(local_gradient["local_grad_%d" % FLAGS.task_index].indices, [-1, ])))

    # Calculate prediction Error
    with tf.device("/job:worker/task:0"):
        assign_w_copy = w_copy.assign(w)
        total_error = tf.Variable(tf.constant([0.0]))
        test_reader = tf.TFRecordReader(name="test_reader")
        test_filename_queue = tf.train.string_input_producer([dataPath_prefix + "/tfrecords22"], num_epochs=None)
        _, test_serialized_example = test_reader.read(test_filename_queue)
        test_features = tf.parse_single_example(test_serialized_example,
                               features={
                                'label': tf.FixedLenFeature([1], dtype=tf.int64),
                                'index' : tf.VarLenFeature(dtype=tf.int64),
                                'value' : tf.VarLenFeature(dtype=tf.float32),
                               }
                              )
        test_label = test_features['label']
        test_index = test_features['index']
        test_value = test_features['value']

        w_small_test = tf.gather(w_copy, test_index.values)
        dot_product_test = tf.reduce_sum(tf.mul(test_value.values, w_small_test))
        val = tf.sign(tf.mul(dot_product_test, tf.cast(test_label, tf.float32)))
        is_error = tf.abs(tf.minimum(tf.constant([0.0]), val))
        add_error = total_error.assign(tf.add(total_error, is_error))

        reset_test_reader = test_reader.reset()
        reset_total_error = total_error.assign(tf.constant([0.0]))

    with tf.Session("grpc://vm-11-%d:2222" % (FLAGS.task_index+1)) as sess:
        # this is new command and is used to initialize the queue based readers.
        # Effectively, it spins up separate threads to read from the files
        if FLAGS.task_index == 0:
            sess.run(tf.initialize_all_variables())

        coord = tf.train.Coordinator()
        threads = tf.train.start_queue_runners(coord=coord)
        for i in range(0, num_iterations):
            start = datetime.datetime.now()
            sess.run(update_w)
            print i, datetime.datetime.now() - start
            if (i % 100) == 0 and FLAGS.task_index == 0:
		sess.run(assign_w_copy)
                for j in range(0, num_test_examples):
                    err = sess.run(add_error)
                print i, err
                sess.run([reset_test_reader, reset_total_error])
        coord.request_stop()
        coord.join(threads)
