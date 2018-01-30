//package org.myorg;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class AnagramSorter {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        private Text key_word = new Text();
        private Text value_word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
		String word = tokenizer.nextToken();
		char[] chars_word = word.toCharArray();
		Arrays.sort(chars_word);
		key_word.set(new String(chars_word));
                value_word.set(word);
                output.collect(key_word, value_word);
            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, NullWritable, Text> {
        private Text key_anagram = new Text();
        private Text value_anagram = new Text();

        public void reduce(Text key, Iterator<Text> values, OutputCollector<NullWritable, Text> output, Reporter reporter) throws IOException {
	    String words = "";
            while (values.hasNext()) {
                Text anagram = values.next();
		words += anagram.toString() + " ";
            }
	    key_anagram.set(key.toString());
	    value_anagram.set(words);
            output.collect(NullWritable.get(), value_anagram);
        }
    }

    public static class sortComparator extends WritableComparator {
	protected sortComparator() {
	    super(Text.class, true);
	}

	@Override
	public int compare(WritableComparable w1, WritableComparable w2){
	    Text k1 = (Text)w1;
	    Text k2 = (Text)w2;
	    String s1 = k1.toString();
	    String s2 = k2.toString();
	    int l1 = s1.length();
	    int l2 = s2.length();
	    if (l1 < l2){
		return -1;
	    } else if (l1 > l2) {
		return 1;
	    } else {
		return  w1.compareTo(w2);
	    }
	}
    }

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(AnagramSorter.class);
        conf.setJobName("AnagramSorter");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

	conf.setOutputKeyComparatorClass(sortComparator.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }
}

