#!/bin/bash

source ~/run.sh

start_all

rm -rf AnagramSorter_Classes/
rm -rf ac.jar
hadoop fs -rmr /output/

mkdir AnagramSorter_Classes/

javac -classpath /home/ubuntu/software/hadoop-2.6.0:/home/ubuntu/conf:/home/ubuntu/software/hive-1.2.1:/home/ubuntu/software/tez-0.7.1-SNAPSHOT-minimal/*:/home/ubuntu/software/tez-0.7.1-SNAPSHOT-minimal/lib/*::/home/ubuntu/conf:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/common/lib/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/common/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/hdfs:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/hdfs/lib/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/hdfs/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/yarn/lib/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/yarn/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/mapreduce/lib/*:/home/ubuntu/software/hadoop-2.6.0/share/hadoop/mapreduce/* -d AnagramSorter_Classes/ AnagramSorter.java

jar -cvf ac.jar -C AnagramSorter_Classes/ .

hadoop jar ac.jar AnagramSorter /input/ /output/
