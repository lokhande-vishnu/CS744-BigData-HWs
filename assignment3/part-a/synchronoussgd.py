import tensorflow as tf
import datetime
import os

dataPath_prefix = '/home/ubuntu/tf/TF_Scripts/uploaded-scripts/data/criteo-tfr'
# number of features in the criteo dataset after one-hot encoding
num_features = 33762578
num_test_examples = 15000
num_iterations = 20
learning_rate = .01

def getFilenames(machine_index):
    if (machine_index in [0, 1]) :
        return [dataPath_prefix + "/tfrecords0" + str(i) for i in range(machine_index*5, (machine_index+1)*5)]
    if (machine_index in [2, 3]) :
        return [dataPath_prefix + "/tfrecords" + str(i) for i in range(machine_index*5, (machine_index+1)*5)]
    return [dataPath_prefix + "/tfrecords2" + str(i) for i in range(0, 2)]

g = tf.Graph()

with g.as_default():

    # creating a model variable on task 0. This is a process running on node vm-11-1
    with tf.device("/job:worker/task:0"):
        #w = tf.Variable(tf.random_normal([num_features], stddev=0.35), name="model")
	w = tf.Variable(.1*tf.ones([num_features]), name="model")

    local_gradient = {}
    for i in range(0, 5):
        with tf.device("/job:worker/task:%d" % i):
            reader = tf.TFRecordReader(name="reader_%d" % i)
            filename_queue= tf.train.string_input_producer(getFilenames(i), num_epochs=num_iterations, name = "queue_%d" % i)
            _, serialized_example = reader.read(filename_queue, name = "serialized_example_%d" % i)

            features = tf.parse_single_example(serialized_example,
                                       features={
                                        'label': tf.FixedLenFeature([1], dtype=tf.int64),
                                        'index' : tf.VarLenFeature(dtype=tf.int64),
                                        'value' : tf.VarLenFeature(dtype=tf.float32),
                                       }, name = "features_%d" % i
                                      )

            label = tf.cast(features['label'], tf.float32, name = "label_%d" % i)
            index = features['index']
            value = features['value']

	with tf.device("/job:worker/task:0"):
            w_small = tf.gather(w, index.values, name='w_small_%d' %i)
	 
	with tf.device("/job:worker/task:%d" %i):
	    m = tf.mul(value.values, w_small)
	    dot_product = tf.reduce_sum(m)
 	    sigmoid = tf.sigmoid(tf.mul(label, dot_product))
            local_gradient_val = tf.mul(value.values, learning_rate*tf.mul(label, tf.sub(sigmoid, 1)))
	
	#with tf.device("/job:worker/task:0"):
	    local_gradient["local_grad_%d" % i] = tf.SparseTensor(indices=tf.reshape(index.values, [-1, 1]), values = local_gradient_val, shape=[num_features])

    # we create an operator to aggregate the local gradients
    with tf.device("/job:worker/task:0"):
	sparse_contribs = tf.SparseTensor(indices=[[0]], values = [0.0], shape=[num_features])
	for i in range(0, 5):
	    sparse_contribs = tf.sparse_add(sparse_contribs, local_gradient['local_grad_%d' %i])
	assign_w = w.scatter_sub(tf.IndexedSlices(sparse_contribs.values, tf.reshape(sparse_contribs.indices, [-1, ])))

    # Calculate prediction Error
    with tf.device("/job:worker/task:0"):
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

        w_small_test = tf.gather(w, test_index.values)
        dot_product_test = tf.reduce_sum(tf.mul(test_value.values, w_small_test))
        val = tf.sign(tf.mul(dot_product_test, tf.cast(test_label, tf.float32)))
        is_error = tf.abs(tf.minimum(tf.constant([0.0]), val))
        add_error = total_error.assign(tf.add(total_error, is_error))

        reset_test_reader = test_reader.reset()
        reset_total_error = total_error.assign(tf.constant([0.0]))

    with tf.Session("grpc://vm-11-1:2222", config=tf.ConfigProto(log_device_placement=True)) as sess:
        # this is new command and is used to initialize the queue based readers.
        # Effectively, it spins up separate threads to read from the files
        sess.run(tf.initialize_all_variables())
        sess.run(tf.initialize_local_variables())
	coord = tf.train.Coordinator()
        threads = tf.train.start_queue_runners(coord=coord)
        for i in range(0, num_iterations):
	    start = datetime.datetime.now()
	    sess.run(assign_w)
	    print i, datetime.datetime.now() - start
	    if (i < 100 and (i % 5) == 0) or (i % 50) == 0:
	        for j in range(0, num_test_examples):
		    err = sess.run(add_error)
	        print i, err
	    	sess.run([reset_test_reader, reset_total_error])
	    #tf.train.SummaryWriter("%s/synchronousSGD" % (os.environ.get("TF_LOG_DIR")), sess.graph)    
	coord.request_stop()
	coord.join(threads)   
