/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.starter.bolt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class FilteredWordCountPrinterBolt extends BaseBasicBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8211319325077781217L;
	private static long count = 0;
	private String tweetFilename = "/home/ubuntu/grader_assign2/Part-B/Question2_tweets.txt";
	private String frequentWordFilename = "/home/ubuntu/grader_assign2/Part-B/Question2_words.txt";

	public FilteredWordCountPrinterBolt() {
		super();
	}

	public FilteredWordCountPrinterBolt(String tweetFile, String frequentWordFile) {
		super();
		this.tweetFilename = tweetFile;
		this.frequentWordFilename = frequentWordFile;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		ArrayList<String> tweetData = (ArrayList<String>) tuple.getValueByField(FilteredWordCountBolt.FILTERED_TWEETS);
		if (tweetData != null) {
			try {
				BufferedWriter writer;
				if (count == 0) {
					writer = new BufferedWriter(new FileWriter(tweetFilename));
				} else {
					writer = new BufferedWriter(new FileWriter(tweetFilename, true));
				}
				for (String string : tweetData) {
					writer.write(string + "\n");
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HashMap<String, Integer> wordCount = (HashMap<String, Integer>) tuple
				.getValueByField(FilteredWordCountBolt.WORD_COUNT_MAP);
		if (wordCount != null) {
			List<Map.Entry<String, Integer>> list = new ArrayList<>(wordCount.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});
			try {
				BufferedWriter writer;
				if (count == 0) {
					writer = new BufferedWriter(new FileWriter(frequentWordFilename));
				} else {
					writer = new BufferedWriter(new FileWriter(frequentWordFilename, true));
				}
				for ( int i = list.size()/2;i<list.size();i++) {
					Entry<String, Integer> element = list.get(i);
					writer.write(element.getKey() + " ");
				}
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		count++;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer ofd) {
	}

}
