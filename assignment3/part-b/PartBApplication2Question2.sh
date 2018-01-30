#!/bin/bash
source ~/run.sh
stop_spark
start_spark
echo "************* Submitting Spark Job ************"
spark-submit --class PartBApplication2Question2 /home/ubuntu/grader_assign3/part-b/GraphXApplication2/GraphXApplication2.jar
echo "**************** Run Completed ****************"

