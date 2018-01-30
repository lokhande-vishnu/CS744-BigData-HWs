1) PartCQuestion1.py
 - This file implements Page Rank with no custom partitioning. The number of partitions has been set to 20 to maximmize resource utilization.

Usage: spark-submit PartCQuestion1.py web-BerkStan.txt 10


2) PartCQuestion2.py 
 - This file partitions ranks RDD according to LINKS RDD partition. This enables join to be computed in parallel and saves on network shuffle.

Usage: spark-submit PartCQuestion2.py web-BerkStan.txt 10 


3) PartCQuestion3.py
 - This file persists LINKS RDD across the iterations of computing page rank. This helps reduce network usage and also saves on computation time.

Usage: spark-submit PartCQuestion3.py web-BerkStan.txt 10

