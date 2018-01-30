import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PartBApplication1Question1PregelVersion {

  def main(args: Array[String]) {

    def vertexProgram(id: VertexId, attr: Double, msgSum: Double): Double =
      0.15 + (1.0 - 0.15) * msgSum

    def sendMessage(edge: EdgeTriplet[Double, Double]): Iterator[(VertexId, Double)] =
      Iterator((edge.dstId, edge.srcAttr * edge.attr))

    def messageCombiner(a: Double, b: Double): Double = a + b

    if (args.length != 1) {
      System.err.println("Incorrect number of arguments, dataset location required")
      System.exit(1);
    }

    val numIterations = 20
    val conf = new SparkConf()
      .setAppName("CS-744-Assignment3-Application1-Question1-PregelVersion")
      .set("spark.locality.wait", "0")
      .set("spark.driver.memory", "1g")
      .set("spark.executor.memory", "18g")
      .set("spark.executor.instances", "5")
      .set("spark.executor.cores", "4")
      .set("spark.task.cpus", "1")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.logLineage", "false")
      .set("spark.master", "spark://10.254.0.86:7077")

    //  Initialize spark context
    val sc = new SparkContext(conf)

    val graph = GraphLoader.edgeListFile(sc, args(0))

    // Initializing the ranks graph with verted attribute values as 1.0 and edge attribute values as 1 / out_degree
    // 'TripletFields.Src' helps populate only necessary fields, thereby improving efficiency
    val ranks: Graph[Double, Double] = graph
      .outerJoinVertices(graph.outDegrees) {
        (vid, data, optDeg) => optDeg.map(_.toDouble).getOrElse(0.0)
      }
      .mapTriplets(edge => (1.0 / edge.srcAttr)) //, TripletFields.Src)
      .mapVertices((vid, data) => 1.0)
      .cache()

    val ranksFinal = Pregel(ranks, 0.0, 20)(vertexProgram, sendMessage, messageCombiner)

    //val results = ranks.vertices.collect()
    //  results.foreach(result => println("url: " + result._1 + " rank: " + result._2))
    ranksFinal.vertices.saveAsTextFile("/pagerank_results")

    sc.stop()
  }
}
