import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PartBApplication2Question2 {
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
    val conf = new SparkConf().setAppName("GraphXApp2Question2")
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
    //send messages across vertex
    val neighborCount: Array[(VertexId,(Int, Int))] = graph.aggregateMessages[(Int, Int)](
      triplet => { // Map Function
        // Send message to destination vertex containing counter and wordlist size
        triplet.sendToDst(1, triplet.dstAttr.length)
      },
      // Add neighbour counter and word entries in neighbour
      (a, b) => (a._1 + b._1, a._2 ) // Reduce Function
    ).collect()
    val maxNeighbor = neighborCount.maxBy(_._2._1)
    val candidates = neighborCount.filter{case(id,(count,wordSize))=> count==maxNeighbor._2._1}
    if(candidates.length==1){
      println("Most Popular Vertex is: "+candidates(0)._1+" with "+candidates(0)._2._1+
        " neighbors and word list of size "+candidates(0)._2._2)
    } else {
      val maxWordSize = candidates.maxBy(_._2._2)
      val finalCandidates = candidates.filter{case(id,(count,wordSize))=> wordSize==maxWordSize._2._2}
      println("Most Popular Vertex is: "+finalCandidates(0)._1+" with "+finalCandidates(0)._2._1+
        " neighbors and word list of size "+finalCandidates(0)._2._2)
    }
  }
}
