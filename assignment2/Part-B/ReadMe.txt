Question1:
The main program for question 1 is PrintEnglishTweets.java
Compile it go to path /home/ubuntu/grader_assign2/Part-B/storm-starter/ and run the below mentioned command

mvn clean install -DskipTests=true; mvn package -DskipTests=true

Run it in local mode using the following command:

storm jar /home/ubuntu/grader_assign2/Part-B/storm-starter/target/storm-starter-1.0.2.jar org.apache.storm.starter.PrintEnglishTweets

Run it on the cluster using the following command:

storm jar /home/ubuntu/grader_assign2/Part-B/storm-starter/target/storm-starter-1.0.2.jar org.apache.storm.starter.PrintEnglishTweets cluster

Output file is a txt file in local file system by the Question1.txt at the location /home/ubuntu/grader-assign2/Part-B/ on the 
virtual machine at which the PrintEnglishBolt (print bolt) was running. This could be checked by looking at the storm UI. Usually it runs on vm-11-2. 

Question2:
The main program for question 2 is RollingFrequentWords.java
Compile it go to path /home/ubuntu/grader_assign2/Part-B/storm-starter/ and run the below mentioned command

mvn clean install -DskipTests=true; mvn package -DskipTests=true

Run it in local mode using the following command:

storm jar /home/ubuntu/grader_assign2/Part-B/storm-starter/target/storm-starter-1.0.2.jar org.apache.storm.starter.RollingFrequentWords slidingWindowWordCount local

Run it on the cluster using the following command:

storm jar /home/ubuntu/grader_assign2/Part-B/storm-starter/target/storm-starter-1.0.2.jar org.apache.storm.starter.RollingFrequentWords slidingWindowWordCount remote

output files are txt files in local file system by name Question2_tweets.txt for tweets and Question2_words.txt for words.
These both files are located at path: /home/ubuntu/grader_assign2/Part-B/ on the virtual machine at which FilteredWordCountPrinterBolt (wordPrinter bolt)
 is running. This could be determined by looking at the storm UI. Usually it runs on vm-11-2.