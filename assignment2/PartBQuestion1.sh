#!/bin/bash
source ~/run.sh
echo "******** Starting Storm ***********"
storm nimbus&
ssh vm-11-2 'storm supervisor&'
ssh vm-11-3 'storm supervisor&'
ssh vm-11-4 'storm supervisor&'
ssh vm-11-5 'storm supervisor&'
storm ui&
echo "******** Compiling and Building Project ***********"
cd /home/ubuntu/grader_assign2/Part-B/storm-starter/
mvn clean install -DskipTests=true; mvn package -DskipTests=true
echo "******** Starting Application ***********"
storm jar /home/ubuntu/grader_assign2/Part-B/storm-starter/target/storm-starter-1.0.2.jar org.apache.storm.starter.PrintEnglishTweets cluster
echo "******** Output at /home/ubuntu/grader_assign2/PartB/Question1.txt in the virtual machine where PrintEnglishBolt is running ***********"
echo "Usually output is in this directory at vm-11-2 machine"