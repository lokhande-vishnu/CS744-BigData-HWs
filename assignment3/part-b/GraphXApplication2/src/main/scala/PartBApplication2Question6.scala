import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PartBApplication2Question6 {
  def checkIntersection(strings: Array[String], tuples: Array[(String, Int)]): Boolean = {
    tuples.foreach{pair =>
      if(strings.contains(pair._1)){
        return true
      }
    }
    return false
  }

  def main(args:Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GraphXApp2Question6")
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
    println("Time intervals that contain most popular word "+maxKey._1+": ")
    var count = 0;
    vertexRDD.collect().foreach { entry =>
      if(entry._2.contains(maxKey._1)){
        print(entry._1+", ")
        count += 1
      }
    }
    println("\nTotal intervals are: "+count)
  }
}

