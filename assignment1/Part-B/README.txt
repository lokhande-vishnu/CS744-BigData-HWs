/* CLASS DESCRIPTION */
1. Map class
The map class implements the mapper function. The output of the mapper function is a key value pair. Value is the word present in the file and key is the same word with it's characters sorted alphabetically. Such a key helps in grouping the anagrams together in the reduce operation which follows.

2. Reduce class
The reduce class implements the reducer function. The reducer function groups the anagrams together and outputs them as one string where each anagram is separated by a space. The key is set to NULL as we do not need to print any key in the final output.

3. sortComparator class
This class is used to implement the compare function which is used to compare two keys. The way we compare two keys decides the order in which the values are displayed in the output. Two keys are first compared on their length. If the length is same then the actual keys are compared.


/* RUNNING THE CODE USING BASH SCRIPT*/
Run the run_partb.sh script present in the directory named Part-B. This generates a file called "part-00000" in the directory called "output" on the HDFS. "run_partb.sh" assumes that all the source files are present in the same directory as the scipt.

There is another script called Part-B.sh. This script assumes that the code is present in a sub-directory called "Part-B".

/* RUNNING THE CODE USING MAKE*/
1.  Run, make
// make compiles the java code and creates the jar file
// Install make if not present already
// If compiled files already exit then run make clean to remove them

2. Run, hadoop jar ac.jar AnagramSorter /input/ /output/
//  input.txt is present in a directory input under root
// output files are created in the directory output under root
// If output directory already exits then delete it by running hadoop fs -rmr /output/

3. The output is present in a file name part-0000 in the output directory

/* NOTE */
1. Punctuation marks haven't been taken care of. 
2. Strings are considered as they are and have not been transformed to lowercase or uppercase
