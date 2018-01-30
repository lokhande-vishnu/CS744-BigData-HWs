#!/bin/bash
echo "******** Moving to the correct directory ***********"
cd ~/
source ~/run.sh
cd ~/grader_assign2

echo "*********** Clearing Cache *************"
Part-A/clear_cache.sh
echo "************ Cache cleared *************"

echo "******** Removing output_partA2 and checkpoint_partA2 directories ***********"
hadoop fs -rm -r /output_partA2 /checkpoint_partA2

echo "******** Creating new output_partA2 and checkpoint_partA2 directories ***********"
hadoop fs -mkdir /output_partA2 /checkpoint_partA2

echo "******** Submitting the spark job ***********"
spark-submit Part-A/PartAQuestion2.py /monitoring
echo "**************** Run Completed *************"
