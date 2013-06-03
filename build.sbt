name := "emdr-scala"

version := "0.0.1"

scalaVersion := "2.10.1"

libraryDependencies ++= List(
  "com.typesafe" % "config" % "1.0.1",
  "com.typesafe.akka" %% "akka-zeromq" % "2.1.2",
  "org.json4s" %% "json4s-native" % "3.2.2",
  "org.mongodb" %% "casbah" % "2.6.0",
  "com.typesafe.slick" %% "slick" % "1.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  //"net.sf.ehcache" % "ehcache-core" % "2.6.5",
  "ch.qos.logback" % "logback-classic" % "1.0.11",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)


