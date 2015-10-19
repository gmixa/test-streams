val akkaStreams = "com.typesafe.akka" %% "akka-stream-experimental" % "1.0"
val akkaStreamsTestkit = "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "1.0" % Test

lazy val test_streams = (project in file(".")).settings(
  organization := "de.mixas",
  name := "test-streams",
  version := "0.1",
  scalaVersion := "2.11.7",
  libraryDependencies ++=Seq(akkaStreams, akkaStreamsTestkit),
  scalacOptions ++= Seq("-feature")
)