ThisBuild / scalaVersion := "3.1.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "me.aethen"
ThisBuild / organizationName := "example"

val zioVersion = "1.0.13"
val zhttpVersion = "1.0.0.0-RC18"

lazy val root = (project in file("."))
  .settings(
    name := "TutorlyAPI",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.13",
      "dev.zio" %% "zio-test" % "1.0.13" % Test,
      "io.d11" %% "zhttp" % zhttpVersion,
      "io.d11" %% "zhttp-test" % zhttpVersion % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
