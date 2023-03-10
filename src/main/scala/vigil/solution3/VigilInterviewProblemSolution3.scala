package vigil.solution3

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import vigil.LocalStorageManager

import scala.util.Try

class VigilInterviewProblemSolution3(localStorageManager: LocalStorageManager) {

  val spark = new SparkContext("local[4]", "VigilInterviewProblemSolution3", new SparkConf())
  spark.setLogLevel("OFF")
  val ss = SparkSession.builder().getOrCreate()
  import ss.implicits._

  def solveTheProblem(): Unit = {
    localStorageManager.getAllCsvAndTsvFilePath().map { files =>
      val dataframes = files.map { filePath =>
        if (filePath.endsWith(".csv")) {
          ss.read.option("header", true).csv(filePath)
        } else {
          ss.read.option("header", true).option("delimiter", "\\t").csv(filePath)
        }
      }.toVector
      dataframes.map(
        _
          .as[(Option[String], Option[String])]
          .rdd
          .map(kv => (kv._1.flatMap(l => Try(l.toLong).toOption).getOrElse(0L), kv._2.flatMap(l => Try(l.toLong).toOption).getOrElse(0L)))
          .reduceByKey(_ ^ _)
      )
        .reduceLeft { (a, b) =>
          a.union(b).reduceByKey(_ ^ _)
        }
        .toDF()
    }.foreach { result =>
//      result.show()
      result.repartition(1).write.mode(SaveMode.Overwrite).option("delimiter", "\\t").csv(os.pwd + "/output/output.tsv")
    }
  }




}
