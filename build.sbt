
name := "code_challenge_vigil"

version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.9.1"
libraryDependencies += "org.mockito" % "mockito-core" % "3.0.0" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.0.0" % Test

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.1"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"
libraryDependencies +=      "net.debasishg" %% "redisclient" % "3.41"

parallelExecution in Test := false
Test / logBuffered := false

Test / parallelExecution := false




