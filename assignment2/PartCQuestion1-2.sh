#!/bin/sh

cd /home/ubuntu/software/flink-1.3.2

./bin/flink run -c cs744.group10.assignment2.PartCQuestion_1_sliding ~/grader_assign2/partC/code/flink-java-project-0.1.jar /home/ubuntu/grader_assign2/partC/data/

cd ~/grader_assign2/
