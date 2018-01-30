from pyspark.sql import SparkSession
from pyspark.sql.types import StructType
from pyspark.sql.functions import window
import sys


def main():
    
    if len(sys.argv) != 3:
        print('ERROR: Two arguments are required in this format: spark-submit PartAQuestion3.py <hdfs_path_to_listening_directory> <path_to_static_file>')
        sys.exit(1)

    monitoringDir = 'hdfs://10.254.0.86:8020' + str(sys.argv[1])
    fileDir = 'hdfs://10.254.0.86:8020' + str(sys.argv[2])

    spark = SparkSession\
        .builder.appName("PartAQuestion3") \
        .config("spark.executor.cores","4") \
        .getOrCreate()

    userSchema = StructType().add("userA", "integer").add("userB", "integer").add("timestamp","timestamp").add("interaction", "string")

    streamDF = spark \
        .readStream \
        .option("sep", ",") \
        .schema(userSchema) \
        .csv(monitoringDir)
    
    staticFile = spark.sparkContext \
                      .textFile(fileDir) \
                      .flatMap(lambda line: line.split(',')) \
                      .collect()
    
    df = streamDF.filter(streamDF['userA'].isin(staticFile)) \
                 .groupBy('userA') \
                 .count()

    query = df \
        .writeStream \
        .outputMode("complete") \
        .option("truncate", "false") \
        .trigger(processingTime='5 seconds') \
        .option("numRows", 2147483646) \
        .format("console") \
        .start()
    
    query.awaitTermination()

if __name__ == '__main__':
    main()
