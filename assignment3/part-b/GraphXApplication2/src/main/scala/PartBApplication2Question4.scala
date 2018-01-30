import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PartBApplication2Question4 {
  def main(args:Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GraphXApp2Question4")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    //read the file and create a VertexRDD
    val vertexRDD: RDD[(VertexId, Array[String])] = sc.textFile("/graph_input.txt").map(line => {
      val field = line.split(": ")
      val words = field(1).split(" ")
      (field(0).toLong, words)
    })
    val counts = vertexRDD.flatMap{case(id,list) => list}
      .map(word => (word, 1))
      .reduceByKey(_ + _)
    val maxKey = counts.reduce((x, y) => if (x._2 > y._2) x else y)
    println("Word: " + maxKey._1 + " count: " + maxKey._2)
  }
}
