val jsoup = "org.jsoup" % "jsoup" % "1.8.3"
val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

lazy val test_streams = (project in file(".")).settings(
  organization := "de.mixas",
  name := "test-streams-example2",
  version := "0.1",
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(jsoup,scalaTest),
  scalacOptions ++= Seq("-feature")
)