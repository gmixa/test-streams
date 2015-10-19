lazy val test_streams = (project in file(".")).settings(
  organization := "de.mixas",
  name := "test-streams",
  version := "0.1",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature")
)