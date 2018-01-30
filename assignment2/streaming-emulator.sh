echo "******** Stopping and restarting all services ***********"
cd ~/
source ~/run.sh
stop_all
start_all
stop_spark
start_spark
hive --service metastore &
cd ~/grader_assign2
echo "******** Deamons started ***********"

echo "******** Removing staging and monitoring directories ***********"
hadoop fs -rm -r /staging /monitoring

echo "******** Creating new staging and monitoring directories ***********"
hadoop fs -mkdir /staging /monitoring

echo "******** Copying split-dataset into staging directory ***********"
hadoop fs -put ~/grader_assign2/Part-A/split-dataset/* /staging/.

echo "******** Starting the streaming process ***********"
for number in {1..1127}
do
    sleep 5s
    echo "moving ${number}.csv"
    hadoop fs -mv /staging/${number}.csv /monitoring/.
done
echo "******** Streaming complete ***********"
