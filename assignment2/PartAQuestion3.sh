#!/bin/bash
echo "******** Moving to the correct directory ***********"
cd ~/
source ~/run.sh
cd ~/grader_assign2


echo "*********** Clearing Cache *************"
Part-A/clear_cache.sh
echo "************ Cache cleared *************"

echo "******** Submitting the spark job ***********"
spark-submit Part-A/PartAQuestion3.py /monitoring /file.txt
echo "**************** Run Completed *************"

