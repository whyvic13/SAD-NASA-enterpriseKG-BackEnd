name := """climate-service-backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.4"

jacoco.settings

libraryDependencies ++= Seq(
  cache,
  javaWs,
  javaCore,
  "org.springframework" % "spring-context" % "4.1.4.RELEASE",
  "javax.inject" % "javax.inject" % "1",
  "org.springframework.data" % "spring-data-jpa" % "1.7.1.RELEASE",
  "org.springframework" % "spring-expression" % "4.1.4.RELEASE",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.hibernate" % "hibernate-c3p0" % "4.3.7.Final",
  "com.googlecode.efficient-java-matrix-library" % "ejml" % "0.23",
  "org.jgrapht" % "jgrapht-jdk1.5" % "0.7.3",
  "org.apache.mahout" % "mahout-core" % "0.9",
  "org.apache.mahout" % "mahout-integration" % "0.11.0"
)


fork in run := false