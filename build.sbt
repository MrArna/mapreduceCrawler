lazy val commonSettings = Seq(
  version := "final"
)

mainClass in (Compile, run) := Some("Main")

crossPaths := false

autoScalaLibrary := false


javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.0.0-alpha1"
libraryDependencies += "org.apache.tika" % "tika-java7" % "1.13"
libraryDependencies += "org.apache.opennlp" % "opennlp-maxent" % "3.0.3"
libraryDependencies += "org.apache.hadoop" % "hadoop-kafka" % "3.0.0-alpha1"
libraryDependencies += "commons-cli" % "commons-cli" % "1.3.1"
libraryDependencies += "junit" % "junit" % "4.12"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"


lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "nameFinder-Arnaboldi"
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

    