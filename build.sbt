name := "emdr-scala"

version := "0.0.1"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.0.1",
  "org.apache.camel" % "camel-scala" % "2.11.0",
  "org.apache.camel" % "camel-jms" % "2.11.0",       // to override activemq-camel dependency
  "org.apache.camel" % "camel-spring" % "2.11.0",    // to override activemq-camel dependency
  "org.apache.camel" % "camel-test" % "2.11.0" % "test",
  "org.apache.camel" % "camel-http4" % "2.11.0",
  "org.apache.camel" % "camel-mongodb" % "2.11.0",
  "com.typesafe.akka" %% "akka-zeromq" % "2.2.0",
  "com.typesafe.akka" %% "akka-camel" % "2.2.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0",
  "org.apache-extras.camel-extra" % "camel-esper" % "2.11-SNAPSHOT",
  "org.apache.activemq" % "activemq-camel" % "5.8.0",
  "org.apache.kafka" %% "kafka" % "0.8.0-SNAPSHOT" exclude("org.slf4j", "slf4j-simple"),
  "org.json4s" %% "json4s-native" % "3.2.2",
  "org.mongodb" %% "casbah" % "2.6.0",
  "org.reactivemongo" %% "reactivemongo" % "0.10-SNAPSHOT",
  "com.typesafe.slick" %% "slick" % "1.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.0.11",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.apache.commons" % "commons-math3" % "3.2",
  "org.mvel" % "mvel2" % "2.1.5.Final"
)
