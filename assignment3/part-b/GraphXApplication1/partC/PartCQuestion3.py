''' Implementation for PageRank algorithm in Python for running on Spark'''

from pyspark import SparkConf, SparkContext, StorageLevel
from operator import add
import sys
import os

# define global variables
lineSeparator = '\t'

def parseLine(line):
	links = line.split(lineSeparator)
	return links[0], links[1]

def getContribution(node, neighbors, node_rank):
    """Calculates URL contributions to the rank of other URLs."""
    num_neighbors = len(neighbors)
    yield (node, 0)
    for neighbor in neighbors:
        yield (neighbor, node_rank / num_neighbors)

if __name__ == '__main__':

	if len(sys.argv) != 3:
		print 'Incorrect Number of Arguments'
		exit(-1) 
	
	fileName = sys.argv[1]
	numIterations = int(sys.argv[2])
	numPartitions = 20

	# Set spark configurations
        conf = SparkConf().setAppName('CS-744-Assignment1-PartC-3')\
                          .set("spark.locality.wait", "0")\
                          .set("spark.driver.memory", "1g")\
                          .set("spark.executor.memory", "18g")\
                          .set("spark.executor.instances", "5")\
                          .set("spark.executor.cores", "4")\
                          .set("spark.task.cpus", "1")\
                          .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")\
                          .set("spark.logLineage", "false")\
                          .set("spark.master", "spark://10.254.0.86:7077")


	# Initialize the spark context.
	sc = SparkContext(conf = conf)

	# Load the input file containing URL pairs and remove all lines containing comments
	lines = sc.textFile(fileName, numPartitions).filter(lambda line: "#" not in line)

	# Split url1	url2 to [url1, url2]
	links = lines.map(lambda line: parseLine(line)).partitionBy(numPartitions)
	# Map [url1, url2], [url1, url3] to [url1, [url2, url3]] 
	links = links.distinct().groupByKey().persist(StorageLevel.MEMORY_ONLY)

	# Initialize ranks for all URLs
	ranks = links.mapValues(lambda urls: 1.0)

	# Iterate to get Page ranks
	for i in range(numIterations):
		# Calculate neighbor contribution.
		contribs = links.join(ranks).flatMap(lambda (node, (neighbors, node_rank)): getContribution(node, neighbors, node_rank))

		# Re-calculates URL ranks based on neighbor contributions.
		ranks = contribs.reduceByKey(add).mapValues(lambda rank: rank * 0.85 + 0.15)

	## Collects all URL ranks and dump them to console.
	#for (link, rank) in ranks.collect():
	#	print("url: %s rank: %s" % (link, rank))

        ranks.saveAsTextFile("/pagerank_results_old")
                
	sc.stop()
