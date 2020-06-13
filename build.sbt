name := "flink-cep-example"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.flink" %% "flink-cep-scala" % "1.9.3"
//libraryDependencies += "org.apache.flink" %% "flink-scala" % "1.9.3"
libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % "1.9.3"
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka" % "1.9.3"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
