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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.starter.RollingFrequentWords;
import org.apache.storm.starter.spout.RandomHashtagSpout;
import org.apache.storm.starter.spout.RandomIntegerSampleSpout;
import org.apache.storm.starter.tools.NthLastModifiedTimeTracker;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;

import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * This bolt performs rolling counts of incoming objects, i.e. sliding window
 * based counting.
 * <p/>
 * The bolt is configured by two parameters, the length of the sliding window in
 * seconds (which influences the output data of the bolt, i.e. how it will count
 * objects) and the emit frequency in seconds (which influences how often the
 * bolt will output the latest window counts). For instance, if the window
 * length is set to an equivalent of five minutes and the emit frequency to one
 * minute, then the bolt will output the latest five-minute sliding window every
 * minute.
 * <p/>
 * The bolt emits a rolling count tuple per object, consisting of the object
 * itself, its latest rolling count, and the actual duration of the sliding
 * window. The latter is included in case the expected sliding window length (as
 * configured by the user) is different from the actual length, e.g. due to high
 * system load. Note that the actual window length is tracked and calculated for
 * the window, and not individually for each object within a window.
 * <p/>
 * Note: During the startup phase you will usually observe that the bolt warns
 * you about the actual sliding window length being smaller than the expected
 * length. This behavior is expected and is caused by the way the sliding window
 * counts are initially "loaded up". You can safely ignore this warning during
 * startup (e.g. you will see this warning during the first ~ five minutes of
 * startup time if the window length is set to five minutes).
 */
public class FilteredWordCountBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3323327772485148366L;
	private static final Logger LOG = Logger.getLogger(FilteredWordCountBolt.class);
	private static final int DEFAULT_SLIDING_WINDOW_IN_SECONDS = 30;
	public static final String FILTERED_TWEETS = "tweets";
	public static final String WORD_COUNT_MAP = "wordCounts";
	private final int windowLengthInSeconds;
	private OutputCollector collector;
	private NthLastModifiedTimeTracker lastModifiedTracker;
	private HashMap<String, Integer> wordCount;
	private ArrayList<String> tweets;
	private int friendCount = -1;
	private ArrayList<String> hashTags;
	private static final List<String> stopWords = Arrays.asList("a", "about", "above", "after", "again", "against",
			"all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being",
			"below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do",
			"does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had",
			"hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here",
			"here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've",
			"if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't",
			"my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our",
			"ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
			"shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves",
			"then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
			"through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
			"we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who",
			"who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're",
			"you've", "your", "yours", "yourself", "yourselves");

	public FilteredWordCountBolt() {
		this(DEFAULT_SLIDING_WINDOW_IN_SECONDS);
	}

	public FilteredWordCountBolt(int windowLengthInSeconds) {
		this.windowLengthInSeconds = windowLengthInSeconds;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		lastModifiedTracker = new NthLastModifiedTimeTracker(1);
		tweets = new ArrayList<>(200);
		wordCount = new HashMap<>(200);
	}

	@Override
	public void execute(Tuple tuple) {
		if (TupleUtils.isTick(tuple) && lastModifiedTracker.secondsSinceOldestModification() >= windowLengthInSeconds) {
			LOG.debug("Received tick tuple, triggering emit of current window counts");
			emitCurrentWindowCounts();
		} else {
			filterTweetAndCountWords(tuple);
		}
	}

	private void emitCurrentWindowCounts() {
		lastModifiedTracker.markAsModified();
		collector.emit(new Values(tweets, wordCount));
		tweets = new ArrayList<>();
		wordCount = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	private void filterTweetAndCountWords(Tuple tuple) {
		if (tuple.getSourceComponent().equalsIgnoreCase(RollingFrequentWords.RANDOM_INTEGER_SPOUT_ID)) {
			friendCount = (int) tuple.getValueByField(RandomIntegerSampleSpout.INTEGER_SPOUT_OUTPUT_FIELD);
		} else if (tuple.getSourceComponent().equalsIgnoreCase(RollingFrequentWords.TWITTER_SPOUT_ID)) {
			Status status = (Status) tuple.getValueByField("tweet");
			if (friendCount > -1 && hashTags != null && !hashTags.isEmpty() && status != null) {
				if (status.getUser().getFriendsCount() < friendCount
						&& "en".equalsIgnoreCase(status.getUser().getLang())
				 && contailsDesired(status.getHashtagEntities())) {
					tweets.add(status.getText());
					addWordCount(status.getText());
				}

			}
		} else if (tuple.getSourceComponent().equalsIgnoreCase(RollingFrequentWords.HASHTAG_SPOUT_ID)) {
			hashTags = (ArrayList<String>) tuple.getValueByField(RandomHashtagSpout.HASHTAG_SPOUT_OUTPUT_FIELD);
		}
	}

	private void addWordCount(String text) {
		String[] words = text.toLowerCase().split("\\s+");
		for (String string : words) {
			if (!stopWords.contains(string)) {
				if (wordCount.containsKey(string)) {
					Integer count = wordCount.get(string);
					count++;
					wordCount.put(string, count);
				} else {
					wordCount.put(string, 1);
				}
			}
		}
	}

	private boolean contailsDesired(HashtagEntity[] hashtagEntities) {
		if (hashtagEntities != null) {
			ArrayList<String> tags = new ArrayList<>(hashtagEntities.length);
			for (HashtagEntity hashtagEntity : hashtagEntities) {
				tags.add(hashtagEntity.getText().toLowerCase());
			}
			for (String string : hashTags) {
				for (String tag : tags) {
					if (tag.contains(string)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(FILTERED_TWEETS, WORD_COUNT_MAP));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, windowLengthInSeconds);
		return conf;
	}
}
