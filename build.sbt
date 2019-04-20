import sbt.Keys.unmanagedSourceDirectories

name := "paint-batch-optimizer"
organization in ThisBuild := "com.paintbatch"
scalaVersion in ThisBuild := "2.12.7"

val commonSettings = Seq(
  cancelable := true,
  fork := true,
  logBuffered := false,
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ypartial-unification"
  )
)

val V = new {
  val http4s = "0.20.0-RC1"
  val circe = "0.11.1"
}

val dependenciesSettings = Seq(
  resolvers += Resolver.bintrayRepo("ovotech", "maven"),
  libraryDependencies ++= Seq(
    "org.http4s"              %% "http4s-core"               % V.http4s,
    "org.http4s"              %% "http4s-dsl"                % V.http4s,
    "org.http4s"              %% "http4s-blaze-server"       % V.http4s,
    "org.http4s"              %% "http4s-circe"              % V.http4s,
    "org.http4s"              %% "http4s-blaze-client"       % V.http4s,

    "io.circe"                %% "circe-core"                % V.circe,
    "io.circe"                %% "circe-generic"             % V.circe,
    "io.circe"                %% "circe-generic-extras"      % V.circe,
    "io.circe"                %% "circe-parser"              % V.circe,

    "ch.qos.logback"           % "logback-classic"           % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging"          % "3.9.0",

    "org.scalatest"           %% "scalatest"                 % "3.0.7"  % Test
  )
)

lazy val roots = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings ++ dependenciesSettings: _*)
  .settings(
    name := "paint-batch-optimizer",
    unmanagedSourceDirectories in Compile += baseDirectory.value / "generated-src" / "main" / "scala",
    unmanagedSourceDirectories in Test += baseDirectory.value / "generated-src" / "test" / "scala"
  )

