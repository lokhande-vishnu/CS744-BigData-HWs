import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PartBApplication2Question1 {
  def getIntersection(list1: Array[String], list2: Array[String]): Array[String] ={
    var common = Array[String]()
    list1.foreach { word =>
      if(list2.contains(word)){
        common :+= word
      }
    }
    return common;
  }

  def main(args:Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GraphXApp2Question1")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    //read the file and create a VertexRDD
    val vertexRDD: RDD[(VertexId, Array[String])] = sc.textFile("/graph_input.txt").map(line => {
      val field = line.split(": ")
      val words = field(1).split(" ")
      (field(0).toLong, words)
    })
    //create a edge RDD
    val edgeRDD: RDD[Edge[Array[String]]] = vertexRDD.cartesian(vertexRDD).filter {
      case ((srcId, srcwrds), (desId, deswrds)) => srcId != desId
    }.map {
      case ((srcId, srcwrds), (desId, deswrds)) => Edge(srcId, desId, getIntersection(srcwrds, deswrds))
    }
      .filter(edge => edge.attr.length != 0)
    val graph = Graph(vertexRDD, edgeRDD)
    val count = graph.triplets.filter { triplet =>
      triplet.srcAttr.length > triplet.dstAttr.length }.count()
    println("Total number of edges satisfying the criteria are: "+count)
  }
}
