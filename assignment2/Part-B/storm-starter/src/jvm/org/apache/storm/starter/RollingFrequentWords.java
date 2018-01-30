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
package org.apache.storm.starter;

import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.starter.bolt.FilteredWordCountBolt;
import org.apache.storm.starter.bolt.FilteredWordCountPrinterBolt;
import org.apache.storm.starter.spout.RandomHashtagSpout;
import org.apache.storm.starter.spout.RandomIntegerSampleSpout;
import org.apache.storm.starter.spout.TwitterSampleSpout;
import org.apache.storm.starter.util.StormRunner;
import org.apache.storm.topology.TopologyBuilder;

/**
 * This topology does a continuous computation of the top N words that the
 * topology has seen in terms of cardinality. The top N computation is done in a
 * completely scalable way, and a similar approach could be used to compute
 * things like trending topics or trending images on Twitter.
 */
public class RollingFrequentWords {

	private static final Logger LOG = Logger.getLogger(RollingTopWords.class);
	private static final int DEFAULT_RUNTIME_IN_SECONDS = 7200;
	public static final String TWITTER_SPOUT_ID = "twitter";
	public static final String RANDOM_INTEGER_SPOUT_ID = "RandomNumber";
	public static final String HASHTAG_SPOUT_ID = "hashtags";
	public static final String WORD_COUNTER_BOLT_ID = "wordCounter";
	public static final String FREQUENT_WORD_PRINTER_BOLD_ID = "wordPrinter";

	private final TopologyBuilder builder;
	private final String topologyName;
	private final Config topologyConfig;
	private final int runtimeInSeconds;

	public RollingFrequentWords(String topologyName) throws InterruptedException {
		builder = new TopologyBuilder();
		this.topologyName = topologyName;
		topologyConfig = createTopologyConfiguration();
		runtimeInSeconds = DEFAULT_RUNTIME_IN_SECONDS;

		wireTopology();
	}

	private static Config createTopologyConfiguration() {
		Config conf = new Config();
		conf.setDebug(true);
		return conf;
	}

	private void wireTopology() throws InterruptedException {
		String consumerKey = "vdzGMqHWJpLgKL3nzd75bvJ11";
		String consumerSecret = "dDF4QAh6UyqBYxWnfIlSR2Zyx8TZhRI0yOP0wX0HYbjHdyB4rF";
		String accessToken = "880568170754527232-DjFfe86MxZ6WjGxsUYMLmXel4mybM1L";
		String accessTokenSecret = "O1ohJenjO8iKokgMaidu9x6YfLdH4bQfnmY4266XfR8MW";
		String[] keyWords = new String[0];
		builder.setSpout(TWITTER_SPOUT_ID,
				new TwitterSampleSpout(consumerKey, consumerSecret, accessToken, accessTokenSecret, keyWords));
		builder.setSpout(RANDOM_INTEGER_SPOUT_ID, new RandomIntegerSampleSpout());
		builder.setSpout(HASHTAG_SPOUT_ID, new RandomHashtagSpout());
		builder.setBolt(WORD_COUNTER_BOLT_ID, new FilteredWordCountBolt(30)).shuffleGrouping(TWITTER_SPOUT_ID)
				.shuffleGrouping(RANDOM_INTEGER_SPOUT_ID).shuffleGrouping(HASHTAG_SPOUT_ID);
		builder.setBolt(FREQUENT_WORD_PRINTER_BOLD_ID, new FilteredWordCountPrinterBolt())
				.shuffleGrouping(WORD_COUNTER_BOLT_ID);
	}

	public void runLocally() throws InterruptedException {
		
		StormRunner.runTopologyLocally(builder.createTopology(), topologyName, topologyConfig, runtimeInSeconds);
	}

	public void runRemotely() throws Exception {
		topologyConfig.setNumWorkers(4);
		StormSubmitter.submitTopologyWithProgressBar(topologyName, topologyConfig, builder.createTopology());
	}

	/**
	 * Submits (runs) the topology.
	 *
	 * Usage: "RollingFrequentWords [topology-name] [local|remote]"
	 *
	 * By default, the topology is run locally under the name
	 * "slidingWindowCounts".
	 *
	 * @param args
	 *            First positional argument (optional) is topology name, second
	 *            positional argument (optional) defines whether to run the
	 *            topology locally ("local") or remotely, i.e. on a real cluster
	 *            ("remote").
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String topologyName = "slidingWindowCounts";
		if (args.length >= 1) {
			topologyName = args[0];
		}
		boolean runLocally = true;
		if (args.length >= 2 && args[1].equalsIgnoreCase("remote")) {
			runLocally = false;
		}

		LOG.info("Topology name: " + topologyName);
		RollingFrequentWords rtw = new RollingFrequentWords(topologyName);
		if (runLocally) {
			LOG.info("Running in local mode");
			rtw.runLocally();
		} else {
			LOG.info("Running in remote (cluster) mode");
			rtw.runRemotely();
		}
	}
}
