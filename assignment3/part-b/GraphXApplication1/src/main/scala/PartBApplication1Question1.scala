import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PartBApplication1Question1 {
  def main(args: Array[String]) {

    if (args.length != 1) {
      System.err.println("Incorrect number of arguments, dataset location required")
      System.exit(1);
    }

    val numIterations = 20
    val conf = new SparkConf()
      .setAppName("CS-744-Assignment3-Application1-Question1")
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
    // sc.setLogLevel("ERROR")

    val graph = GraphLoader
      .edgeListFile(sc, args(0))
      .partitionBy(PartitionStrategy.EdgePartition2D)

    // Initializing the ranks graph with verted attribute values as 1.0 and edge attribute values as 1 / out_degree
    // 'TripletFields.Src' helps populate only necessary fields, thereby improving efficiency
    val outDeg = graph.outDegrees
    var ranks: Graph[Double, Double] = graph
      .outerJoinVertices(outDeg) {
        (vid, data, optDeg) => optDeg.map(_.toDouble).getOrElse(0.0)
      }
      .mapTriplets(edge => (1 / edge.srcAttr), TripletFields.Src)
      .mapVertices((vid, data) => 1.0)

    // Looping for 20 iteration to update the ranks graph
    var i = 0
    var prevRanks: Graph[Double, Double] = null
    while (i < numIterations) {
      println(s"************************************************ iteration $i / $numIterations")
      ranks.cache() // caches the graph to memory

      // Creating a contribs RDD which which stores the contributions recieved from the previous nodes
      val contribs = ranks
        .aggregateMessages[Double](ctx => ctx.sendToDst(ctx.srcAttr * ctx.attr), _ + _, TripletFields.Src)

      prevRanks = ranks
      // New ranks are computed
      ranks = ranks.outerJoinVertices(contribs) {
        (vid, oldRank, contribution) => 0.85*contribution.getOrElse(0.0) + 0.15
      }.cache()

      ranks.edges.foreachPartition(x => {})
      prevRanks.vertices.unpersist(false)
      prevRanks.edges.unpersist(false)

      i += 1
    }

    // val results = ranks.vertices.collect()
    // results.foreach(result => println("url: " + result._1 + " rank: " + result._2))
    ranks.vertices.saveAsTextFile("/pagerank_results")
    sc.stop()
  }
}
