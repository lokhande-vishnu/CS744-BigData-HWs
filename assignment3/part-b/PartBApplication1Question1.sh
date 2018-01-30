#!/bin/bash

echo "*********** Restarting cluster services *************"
source ~/run.sh
cd ~/
stop_all
stop_spark
start_all
start_spark
cd ~/grader_assign3/part-b/
hadoop fs -rm -r  /pagerank_results
echo "*********** Cluster services have been restarted *************"

echo "*********** Clearing Cache *************"
GraphXApplication1/clear_cache.sh
echo "************ Cache cleared *************"

echo "*********** Copying disk and network stats ************"
GraphXApplication1/prerun_stat.sh
echo "************* Pre run stats copied *************"

echo "************* Creating Jars  *************"
cd GraphXApplication1
mvn clean
mvn package
cd  ../
echo "************* Jars Created *************"

echo "************* Submitting Spark Job ************"
spark-submit --class PartBApplication1Question1 GraphXApplication1/target/PageRank-0.0.1.jar /soc-LiveJournal1.txt
echo "**************** Run Completed ****************"

echo "*********** Copying disk and network stats ************"
GraphXApplication1/postrun_stat.sh
echo "*********** Post Run stats copied **************"

echo "******** Bandwidth Results are: ***************"
GraphXApplication1/extract_params2.sh
