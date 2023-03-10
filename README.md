# Vigil Interview Code Challenge


## First Solution
There are two classes which are responsible for:

1. LocalStorageManager reads all the CSV and TSV files and converts data to a List[(Long, Long)].

2. VigilInterviewProblem provides a solution to the problem.

## How this solution works
We gather all the values for a specific key and XOR all the values with each other to find the value that occurs an odd number of times. For example, if key 8 and key 4 are associated with the following values:
```aidl
8 -> 12
8 -> 0
8 -> 0
8 -> 12
8 -> 0

4 -> 22
4 -> 5
4 -> 22
4 -> 6
4 -> 6
```

we are going to calculate xor of following:

we calculate the XOR of the following:

12 ^ 0 ^ 0 ^ 0 ^ 12 ^ 0 = 0

22 ^ 5 ^ 22 ^ 6 ^ 6 = 5

and we append 8 -> 0 and 4 -> 5 to the output.

Here's the source code for the solution:

```scala
  def solveTheProblem(): Unit = {
  println("Starting to calculate the output")
  val result = localStorageManager.readAllKeyValueFromFile()
    .groupBy(_._1)
    .mapValues { vs =>
      vs.map(_._2).foldLeft(0L)(_ ^ _)
    }.toList
  localStorageManager.writeToOutput(result)
}
```
We read all the lines from the files and convert them to a List[(Long, Long)]. Then we group by the key and XOR all the values associated with the same key to get the result.

The time complexity of this algorithm is O(n) (assuming the sum of lines in all the files is n) and the space complexity is M(n). We assume all the data fits in memory, but we'll discuss a solution for handling huge amounts of data that cannot fit in one machine.

## Second Solution

In the previous solution, we assumed that all lines (n) could fit in memory. However, now we need to account for the scenario where we have a massive amount of data that cannot fit in the memory or disk of a single machine. We assume that the /input directory is located on a remote server, which we can mount. Due to memory limitations, we can only read files line by line, as it's possible for a single file to exceed our memory capacity.

We aim to use Redis database to address this specific problem, and we assume that we have multiple nodes in our Redis cluster. Redis utilizes sharding to distribute data across the cluster, and each node stores a portion of the data at any given time.
As the amount of data increases, we can add more nodes to the Redis cluster to accommodate the higher demand.

![image](./redisCluster.png)

Our process involves reading each key from a file and retrieving the associated value from Redis. We will then XOR this value with the new value that we have read from the file and store the result back into Redis. Once we have completed all the files, we will dump all the key-value pairs we have stored into an output file located on a remote server that can accommodate files of any size. By doing so, we can ensure that the data is safely and securely stored without any concerns about file size limitations.

To speed up the process of reading files from the input directory that contains a list of TSV and CSV files, we can distribute the task across multiple threads. and we need to be careful and use some sort of locking mechanism to avoid data corruption. The following Scala code demonstrates this solution(I didn't use multiple threads to avoid any complication in implementation):

```scala

  def solveTheProblem(): Unit = {
    localStorageManager.getAllCsvAndTsvFilePath() foreach { files =>
      files.foreach { filePath =>
        val delimiter = if (filePath.contains("csv")) "," else "\t"
        localStorageManager.readFileLineByLineAndApplyTask(
          filePath = filePath,
          task = normalizeLineAndXorValuesForTheSameKeyInRedis(delimiter)
        )
      }
    }
    redisClient.getKeys.foreach { key =>
      redisClient.get(key).map { value =>
        localStorageManager.writeToOutput(key, value)
      }
    }
    localStorageManager.closeOutput
  }

  def normalizeLineAndXorValuesForTheSameKeyInRedis(delimiter: String)(line: String): Unit = {
    val l = line.split(delimiter)
    val key: Long = Try(l.head.toLong).toOption.getOrElse(0L)
    val value: Long = Try(l(1).toLong).toOption.getOrElse(0L)

    val accXorValue = redisClient.get(key).getOrElse(0L)
    redisClient.set(key, accXorValue ^ value)
    ()
  }
```

This algorithm has a time complexity of O(n) and a space complexity of M(n)..

# Third Solution
As mentioned in the document, to save the results in Spark local, I looked into the Spark documentation and found out that it could be used to solve this particular problem. As I have just started learning Spark around three hours ago and have no prior experience with it, this solution may be unusable in a production environment. The solution is exactly like the first one, except that Spark supports a built-in function called reduceByKey, which we can use instead of groupBy. Here is the code for this solution: 
 ```scala
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
      result.repartition(1).write.mode(SaveMode.Overwrite).option("delimiter", "\\t").csv(os.pwd + "/output/output.tsv")
    }
  }

```

### <span style="color:red"> Note that each test should be run separately.</span>
