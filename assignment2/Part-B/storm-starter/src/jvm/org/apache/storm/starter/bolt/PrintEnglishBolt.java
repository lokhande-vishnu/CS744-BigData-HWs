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
import java.util.Map;

import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.Nimbus.Client;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Utils;

import twitter4j.Status;

public class PrintEnglishBolt extends BaseBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static long count = 0;
	private String filename = "/home/ubuntu/grader_assign2/Part-B/Question1.txt";

	public PrintEnglishBolt() {
		super();
	}

	public PrintEnglishBolt(String filename) {
		super();
		this.filename = filename;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		Status data = (Status) tuple.getValueByField("tweet");
		if (count > 210000) {
			Map conf = Utils.readStormConfig();
			Client client = NimbusClient.getConfiguredClient(conf).getClient();
			conf.put("nimbus.seeds", "localhost");
			KillOptions killOpts = new KillOptions();
			try {
				client.killTopologyWithOpts("PrintEnglishTweets", killOpts);
			} catch (TException e) {
				System.out.println("Error while killing the topology");
			}
		} else if ("en".equalsIgnoreCase(data.getUser().getLang())) {
			try {
				BufferedWriter writer;
				if(count == 0){
					writer = new BufferedWriter(new FileWriter(filename));
				}else {
					writer = new BufferedWriter(new FileWriter(filename, true));
				}
				writer.write(data.getText() + "\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			count++;
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer ofd) {
	}

}
