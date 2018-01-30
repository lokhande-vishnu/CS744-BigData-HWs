#!/bin/bash
echo "******** Stopping all services and starting HDFS and Spark ***********"
cd ~/
source run.sh
stop_all
start_hdfs
start_spark
cd grader
echo "************* Deamons started ************"

echo "*********** Clearing Cache *************"
partC/clear_cache.sh
echo "************ Cache cleared *************"

echo "*********** Copying disk and network stats ************"
partC/prerun_stat.sh
echo "************* Pre run stats copied *************"

echo "************* Submitting Spark Job ************"
spark-submit partC/PartCQuestion1.py web-BerkStan.txt 10
echo "**************** Run Completed ****************"

echo "*********** Copying disk and network stats ************"
partC/postrun_stat.sh
echo "*********** Post Run stats copied ************** "

echo "******** Bandwidth Results are: ***************"
partC/extract_params.sh
