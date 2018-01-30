Application1
Pagerank implementation using graphX api's. 

Instructions to run:
Input file is present on hdfs in the root directory (/soc-LiveJournal1.txt).
Output file (url-pageranks) are computed and put in the root directory under the name	pagerank_results. (/pagerank_results)
Run the code like this
./PartBApplication1Question1.sh

Logic:
A ranks graph keeps updating the pageranks for each iteration. The vertex attibutes of the ranks graph stores the ranks while the edge attributes store the a value of (1/outdegree). A contribs RDD is computed and is joined with the ranks graph in each iteration. Ranks are cached in memory for efficiency.

Application2
Input: For this application input is the output file "Question2_words.txt" of PartB from Assignment2. We have written a python script "language_cleanup.py" to cleanup web addresses, hashtags and non-alphanumeric words and stored it in the new file "graph_input.txt". This file will serve as input for all sub-applications that are developed under Application2.

Question1
Implementation: PartBApplication2Question1.scala

Logic: 
1. Read the "graph_input.txt" file and create a VertexRDD which is a RDD(VertexId,Array[String) using 'map' function.
2. Create EdgeRDD by doing cartesian product of vertexRDD with itself and then filter out the entries where their is no common word between two vertexs.
3. Create a graph using VertexRDD and EdgeRDD.
4. Get the triplet view of the graph and use filter on the condition that source word list should be larger than the destination word list.
5. Get the count of the filtered RDD as answer

Output: For my case of 262 vertex the total number of edges that satisfy the criteria : 33648

Question2
Implementation: PartBApplication2Question2.scala

Logic: 
1. Read the "graph_input.txt" file and create a VertexRDD which is a RDD(VertexId,Array[String) using 'map' function.
2. Create EdgeRDD by doing cartesian product of vertexRDD with itself and then filter out the entries where their is no common word between two vertexs.
3. Create a graph using VertexRDD and EdgeRDD.
4. Get the aggregated message of counter from each neighbor, thereby creating a neighbor RDD which stores the number of neighbors of each vertex.
5. Find the maximum degree in the created RDD.
6. Filter the RDD to only have entries corresponding to maximum degree.
7. If RDD has only one entry than the entry is the answer.
8. Else find the entry which has maximum words and that entry will be the answer.

Output: For my case of 262 vertex the vertex most popular corresponds to time interval : 53

Question3
Implementation: PartBApplication2Question3.scala

Logic: 
1. Read the "graph_input.txt" file and create a VertexRDD which is a RDD(VertexId,Array[String) using 'map' function.
2. Create EdgeRDD by doing cartesian product of vertexRDD with itself and then filter out the entries where their is no common word between two vertexs.
3. Create a graph using VertexRDD and EdgeRDD.
4. Get the aggregated message of word size from each neighbor and reducing it by adding, thereby creating a neighbor RDD which stores the total number of words in neighbors of each vertex.
5. Use map function to take average value on each entry of RDD.
6. Print the RDD values.

Output: The output is list of tuples of the form (VertexId,Average Words in neighbor), printed one vertex per line.

Question4
Implementation: PartBApplication2Question4.scala

Logic: 
1. Read the "graph_input.txt" file and create a VertexRDD which is a RDD(VertexId,Array[String) using 'map' function.
2. Flaten the vertexRDD to get list of words and then reduce them on key to get words and there count.
3. Find the maximum count in the created RDD.
4. Print any entry randomly from the RDD that has its count equal to maximum count.

Output: For my case of 262 vertex the word most popular comes out to be : rt

Question5
Implementation: PartBApplication2Question5.scala

Logic: 
1. Read the "graph_input.txt" file and create a VertexRDD which is a RDD(VertexId,Array[String) using 'map' function.
2. Create EdgeRDD by doing cartesian product of vertexRDD with itself and then filter out the entries where their is no common word between two vertexs.
3. Create a graph using VertexRDD and EdgeRDD.
4. Call the connectedComponents algorithm on this graph to get connected components.
5. Reduce the RDD based on cluster's min vertex and use a counter to keep track of nodes.
6. Print the RDD value that has the maximum count as the biggest cluster.

Output: For my case of 262 vertex the biggest size of subgraph comes to be : 262

Question6
Implementation: PartBApplication2Question6.scala

Logic: 
1. Read the "graph_input.txt" file and create a VertexRDD which is a RDD(VertexId,Array[String) using 'map' function.
2. Flaten the vertexRDD to get list of words and then reduce them on key to get words and there count.
3. Find the maximum count in the created RDD.
4. Locate a word in the RDD that has maximum count.
5. Filter and print all the vertex in vertexRDD that have the popular word in its word list. 

Output: For my case of 262 vertex the all the 262 vertex has the most popular word rt
