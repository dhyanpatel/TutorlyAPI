ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "me.aethen"
ThisBuild / organizationName := "aethen"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerBaseImage := "java:8-jre"
Docker / packageName := "tutorlyapi/api"
Docker / maintainer := "Maintainer"
packageSummary := "tutorly api summary"
packageDescription := "tutorly api description"
dockerRepository := Some("us.gcr.io")

val zioVersion = "1.0.13"
val zhttpVersion = "1.0.0.0-RC21"

lazy val root = (project in file("."))
  .settings(
    name := "TutorlyAPI",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-streams" % zioVersion,
      "io.d11" %% "zhttp" % zhttpVersion,
      "io.d11" %% "zhttp-test" % zhttpVersion % Test,
      "org.postgresql" % "postgresql" % "42.3.1",
      "com.typesafe" % "config" % "1.4.1",
      "io.getquill" %% "quill-jdbc-zio" % "3.12.0"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    mainClass := Some("tutorly.TutorlyServer")
  )
