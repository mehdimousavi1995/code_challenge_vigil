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
