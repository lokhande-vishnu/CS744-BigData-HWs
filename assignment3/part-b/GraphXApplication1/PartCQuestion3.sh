#!/bin/bash

:'
echo "*********** Restarting cluster services *************"
cd ~/
stop_all
stop_spark
start_all
start_spark
cd ~/pgrank/.
echo "*********** Cluster services have been restarted *************"
'
source ~/run.sh
hadoop fs -rm -r  /pagerank_results_old

echo "*********** Clearing Cache *************"
partC/clear_cache.sh
echo "************ Cache cleared *************"

echo "*********** Copying disk and network stats ************"
partC/prerun_stat.sh
echo "************* Pre run stats copied *************"

echo "************* Submitting Spark Job ************"
spark-submit partC/PartCQuestion3.py /soc-LiveJournal1.txt 20
echo "**************** Run Completed ****************"

echo "*********** Copying disk and network stats ************"
partC/postrun_stat.sh
echo "*********** Post Run stats copied **************"

echo "******** Bandwidth Results are: ***************"
partC/extract_params2.sh
