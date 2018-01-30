from pyspark.sql import SparkSession
from pyspark.sql.types import StructType
from pyspark.sql.functions import window
import sys

if __name__ == '__main__':

    if len(sys.argv) != 2:
        print('ERROR: One  argumens is required in this format: spark-submit PartAQuestion3.py <hdfs_path_to_listening_directory>')
        sys.exit(1)

    monitoringDir = 'hdfs://10.254.0.86:8020' + str(sys.argv[1])
    checkpointDir = 'hdfs://10.254.0.86:8020/checkpoint_partA2'
    outputDir = 'hdfs://10.254.0.86:8020/output_partA2'

    spark = SparkSession\
        .builder.appName("Streaming")\
        .config("spark.sql.streaming.checkpointLocation", checkpointDir) \
        .config("spark.executor.cores","4") \
        .getOrCreate()

    userSchema = StructType().add("userA", "integer").add("userB", "integer").add("timestamp","timestamp").add("interaction", "string")

    df = spark \
        .readStream \
        .option("sep", ",") \
        .schema(userSchema) \
        .csv(monitoringDir)
    
    mentionTweets = df.filter(df['interaction'] == 'MT')
    twitterId = mentionTweets.select('userB')

    query = twitterId \
        .writeStream \
        .trigger(processingTime='10 seconds') \
        .format("parquet") \
        .option("path", outputDir) \
        .start()

    query.awaitTermination()
