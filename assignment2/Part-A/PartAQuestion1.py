from pyspark.sql import SparkSession
from pyspark.sql.types import StructType
from pyspark.sql.functions import window
import sys

if __name__ == '__main__':

    if len(sys.argv) != 2:
        print('ERROR: One argument is required in this format: spark-submit PartAQuestion3.py <hdfs_path_to_listening_directory>')
        sys.exit(1)

    monitoringDir = 'hdfs://10.254.0.86:8020' + str(sys.argv[1])

    spark = SparkSession\
        .builder.appName("Streaming")\
        .config("spark.executor.cores","4") \
        .getOrCreate()

    userSchema = StructType().add("userA", "integer").add("userB", "integer").add("timestamp","timestamp").add("interaction", "string")

    csvDF = spark \
        .readStream \
        .option("sep", ",") \
        .schema(userSchema) \
        .csv(monitoringDir)
    
    windowedCounts = csvDF.groupBy(
        window(csvDF.timestamp, "60 minutes", "30 minutes"),
        csvDF.interaction
    ).count() \
    .orderBy("window")

    query = windowedCounts \
        .writeStream \
        .outputMode("complete") \
        .option("truncate", "false") \
        .option("numRows", 2147483646) \
        .format("console") \
        .start()
    
    query.awaitTermination()
