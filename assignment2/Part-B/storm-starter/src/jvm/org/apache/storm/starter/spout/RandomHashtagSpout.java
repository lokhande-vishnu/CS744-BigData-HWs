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
package org.apache.storm.starter.spout;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

/**
 * Emits a random integer and a timestamp value (offset by one day), every 100
 * ms. The ts field can be used in tuple time based windowing.
 */
public class RandomHashtagSpout extends BaseRichSpout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6493582854720860956L;
	private SpoutOutputCollector collector;
	private Random rand;
	private static String[] hashTags = { "food", "halloween", "thanksgiving", "holiday", "day", "movie", "game",
			"favorite", "awesome", "modi", "trump", "morning", "party", "fun", "kill", "weekend", "national", "money",
			"super", "park", "football", "snow", "winter", "love", "together", "forever", "4ever", "puppy", "hiking",
			"enjoy", "dance", "music", "dinner", "date", "lunch", "brunch", "breakfast", "evening", "hiring", "take",
			"give", "free", "bitcoin", "ai", "new", "rt", "job", "win", "cat", "dog", "drive", "got", "playing",
			"state", "lives", "friend", "black", "white", "king", "best", "awsme", "awe", "sleep", "drunk", "fuck",
			"feel", "first", "dream", "top", "stop", "pollution", "cake", "hot", "hurry", "reward", "album", "mpn",
			"united", "fake", "quick", "wordpress", "eliana", "fleek", "hbd", "art" };
	private long waitTime = 50;
	public static String HASHTAG_SPOUT_OUTPUT_FIELD = "hashtagList";

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(HASHTAG_SPOUT_OUTPUT_FIELD, "ts"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.rand = new Random(123);
	}

	@Override
	public void nextTuple() {
		ArrayList<String> selectedTags = new ArrayList<>(hashTags.length);
		for (String string : hashTags) {
			if (rand.nextDouble() <= 0.8) {
				selectedTags.add(string);
			}
		}
		Utils.sleep(waitTime);
		collector.emit(new Values(selectedTags, System.currentTimeMillis() - (24 * 60 * 60 * 1000)));
		if (waitTime == 50) {
			waitTime = 30000;
		}
	}

	@Override
	public void ack(Object msgId) {
	}

	@Override
	public void fail(Object msgId) {
	}
}
