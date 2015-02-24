name := """nochat"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.21",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.notnoop.apns" % "apns" % "1.0.0.Beta6"
)
