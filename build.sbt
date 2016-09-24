lazy val commonSettings = Seq(
  organization := "edu.uic",
  version := "0.1.0"
)

mainClass in (Compile, run) := Some("Main")

crossPaths := false

autoScalaLibrary := false


javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.0.0-alpha1"
libraryDependencies += "org.apache.tika" % "tika-java7" % "1.13"
libraryDependencies += "org.apache.opennlp" % "opennlp-maxent" % "3.0.3"
libraryDependencies += "org.apache.hadoop" % "hadoop-kafka" % "3.0.0-alpha1"

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "cs441-HW2"
  ).
  enablePlugins(AssemblyPlugin)

resolvers in Global ++= Seq(
  "Sbt plugins"                   at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Maven Central Server"          at "http://repo1.maven.org/maven2",
  "TypeSafe Repository Releases"  at "http://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".txt" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

    