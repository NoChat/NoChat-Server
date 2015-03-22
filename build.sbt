name := """nochat"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

resolvers += "Maven Central" at "http://repo1.maven.org/maven2/"

resolvers += "mvnrepository" at "http://mvnrepository.com/artifact/"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.21",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
  "org.apache.shiro" % "shiro-core" % "1.2.1",
  "com.googlecode.json-simple" % "json-simple" % "1.1",
  "net.sf.flexjson" % "flexjson" % "2.0"
)
