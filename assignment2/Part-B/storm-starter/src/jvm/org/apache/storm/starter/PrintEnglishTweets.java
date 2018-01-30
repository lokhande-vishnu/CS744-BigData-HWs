package org.apache.storm.starter;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.starter.bolt.PrintEnglishBolt;
import org.apache.storm.starter.spout.TwitterSampleSpout;
import org.apache.storm.starter.util.StormRunner;
import org.apache.storm.topology.TopologyBuilder;

public class PrintEnglishTweets {
	public static void main(String[] args) throws Exception {
		String consumerKey = "vdzGMqHWJpLgKL3nzd75bvJ11";
		String consumerSecret = "dDF4QAh6UyqBYxWnfIlSR2Zyx8TZhRI0yOP0wX0HYbjHdyB4rF";
		String accessToken = "880568170754527232-DjFfe86MxZ6WjGxsUYMLmXel4mybM1L";
		String accessTokenSecret = "O1ohJenjO8iKokgMaidu9x6YfLdH4bQfnmY4266XfR8MW";
		String[] keyWords = { "america", "president", "war", "kill", "gun", "RT ", "people", "need", "good", "just",
				"terror", "fuck", "india", "love", "lov", "luv", "trump", "politics", "nation", "best", "out", "game",
				"twitter", "tiny", "new", "now", "today", "free","food","i" };

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("twitter",
				new TwitterSampleSpout(consumerKey, consumerSecret, accessToken, accessTokenSecret, keyWords));
		builder.setBolt("print", new PrintEnglishBolt()).shuffleGrouping("twitter");

		Config conf = new Config();
		if (args != null && args.length > 0) {
			conf.setNumWorkers(4);
			StormSubmitter.submitTopologyWithProgressBar("PrintEnglishTweets", conf, builder.createTopology());
		} else {
			StormRunner.runTopologyLocally(builder.createTopology(), "PrintEnglishTweets", conf, 7200);
			/*
			 * LocalCluster cluster = new LocalCluster();
			 * cluster.submitTopology("PrintEnglishTweets", conf,
			 * builder.createTopology()); Utils.sleep();
			 * cluster.killTopology("PrintEnglishTweets"); cluster.shutdown();
			 */
		}
	}
}
