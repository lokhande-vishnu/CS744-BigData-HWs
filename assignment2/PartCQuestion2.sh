#!/bin/sh

cd /home/ubuntu/software/flink-1.3.2

echo '************** Generating output for allowed lateness = 30 *****************'
./bin/flink run -c cs744.group10.assignment2.PartCQuestion_2 ~/grader_assign2/partC/code/flink-java-project-0.1.jar /home/ubuntu/grader_assign2/partC/data/ 30

echo '************** Generating output for allowed lateness = 60 *****************'
./bin/flink run -c cs744.group10.assignment2.PartCQuestion_2 ~/grader_assign2/partC/code/flink-java-project-0.1.jar /home/ubuntu/grader_assign2/partC/data/ 60

echo '************** Generating output for allowed lateness = 100 *****************'
./bin/flink run -c cs744.group10.assignment2.PartCQuestion_2 ~/grader_assign2/partC/code/flink-java-project-0.1.jar /home/ubuntu/grader_assign2/partC/data/ 100

echo '************** Generating output for allowed lateness = 500 *****************'
./bin/flink run -c cs744.group10.assignment2.PartCQuestion_2 ~/grader_assign2/partC/code/flink-java-project-0.1.jar /home/ubuntu/grader_assign2/partC/data/ 500

cd ~/grader_assign2
echo '************** Computing Overlap ********************'
python partC/computeOverlap.py

