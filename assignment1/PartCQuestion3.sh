#!/bin/bash
echo "*********** Clearing Cache *************"
partC/clear_cache.sh
echo "************ Cache cleared *************"

echo "*********** Copying disk and network stats ************"
partC/prerun_stat.sh
echo "************* Pre run stats copied *************"

echo "************* Submitting Spark Job ************"
spark-submit partC/PartCQuestion3.py web-BerkStan.txt 10
echo "**************** Run Completed ****************"

echo "*********** Copying disk and network stats ************"
partC/postrun_stat.sh
echo "*********** Post Run stats copied **************"

echo "******** Bandwidth Results are: ***************"
partC/extract_params.sh
