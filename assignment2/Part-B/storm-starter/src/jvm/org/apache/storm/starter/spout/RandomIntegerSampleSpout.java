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
 * Emits a random integer and a timestamp value (offset by one day),
 * every 100 ms. The ts field can be used in tuple time based windowing.
 */
public class RandomIntegerSampleSpout extends BaseRichSpout {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6493582854720860956L;
    private SpoutOutputCollector collector;
    private Random rand;
    private static int[] numbers = {1000,2000,3000,10000,50000,100000,500000,1000000}; 
    private long waitTime = 50;
    public static String INTEGER_SPOUT_OUTPUT_FIELD = "friendCount";


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(INTEGER_SPOUT_OUTPUT_FIELD, "ts"));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.rand = new Random(123);
    }

    @Override
    public void nextTuple() {
        int num = numbers[rand.nextInt(numbers.length)];
        Utils.sleep(waitTime);
        collector.emit(new Values(num, System.currentTimeMillis() - (24 * 60 * 60 * 1000)));
        if(waitTime == 50){
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
