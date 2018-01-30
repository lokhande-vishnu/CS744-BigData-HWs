###### Streaming emulation ######
>streaming-emulator.sh
Stops and restarts all the processes. 
All the files are first collected in the staging directory on hdfs (/staging) (this takes < 1min)
Periodically (with an interval of 5 secs), files are moved to monitoring directory (/monitoring)

Run the app scripts after streaming-emulator.sh has began the streaming process. 

###### Part-A Question 1 ######
> script - PartAQuestion1.sh
> app - PartAQuestion1.py

Run streaming-emulator.sh before running PartAQuestion1.sh

The app can be separately run as follows-
spark-submit PartAQuestion1.py </path/to/monitoringdir>
The </path/to/monitoringdir> is the path relative to the root on hdfs.
For eg., /monitoring

###### Part-A Question 2 ######
> script - PartAQuestion2.sh
> app - PartAQuestion2.py

Run streaming-emulator.sh before running PartAQuestion2.sh

The app can be separately run as follows-
spark-submit PartAQuestion2.py </path/to/monitoringdir>
The </path/to/monitoringdir> is the path relative to the root on hdfs.
For eg., /monitoring

The output of the app is written on hdfs to the directory- /output_partA2
A checkpoint is created by the app at the location- /checkpoint_partA2

###### Part-A Question 3 ######
> script - PartAQuestion3.sh
> app - PartAQuestion3.py

Run streaming-emulator.sh before running PartAQuestion3.sh
This app requires a file named 'file.txt' to be present in the hdfs directory. We have already put one such file in the directory.

The app can be separately run as follows-
spark-submit PartAQuestion3.py </path/to/monitoringdir> </path/to/static/file>
The </path/to/monitoringdir> and </path/to/static/file> are path relative to the root on hdfs.
For eg., /monitoring 

A file named 'file.txt' has been placed in the root directory of hdfs. The file contains the twitter ids in a comma separated fashion. If you want to use a new file for analysis, place that file on hdfs and give it's path, relative to the root, as a cmd line argument for this app.
For eg., /file.txt



