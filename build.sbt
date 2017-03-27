lazy val scalafix = "ch.epfl.scala" %% "scalafix-core" % "0.3.2"
lazy val io = "org.scala-sbt" %% "io" % "1.0.0-M9"

// These settings are used across all subprojects
lazy val buildLevelSettings = Seq(
  version in ThisBuild      := "0.1.0",
  organization in ThisBuild := "com.eed3si9n",
  description in ThisBuild  := "a demo project for sbt-sidedish",
  homepage in ThisBuild     := Some(url("https://github.com/eed3si9n/sbt-rewritedemo")),
  scmInfo in ThisBuild      := Some(ScmInfo(url("https://github.com/eed3si9n/sbt-rewritedem"), "git@github.com:eed3si9n/sbt-rewritedem.git")),
  licenses in ThisBuild     := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  bintrayOrganization in ThisBuild := None,
  bintrayRepository in ThisBuild   := "sbt-plugins",
  bintrayPackage in ThisBuild      := "sbt-rewritedemo"
)

lazy val root = (project in file("."))
  .aggregate(app, sbtplugin)
  .settings(buildLevelSettings)
  .settings(
    name := "sbt-rewritedemo root"
  )

// command line project that uses scalafix
lazy val app = (project in file("app"))
  .settings(
    name := "rewritedemo",
    scalaVersion := "2.12.1",
    crossScalaVersions := Seq("2.12.1"),
    libraryDependencies ++= List(scalafix, io),
    scalacOptions += "-Ywarn-unused-import",
    resolvers += Resolver.bintrayIvyRepo("scalameta", "maven"),
    bintrayRepository := (bintrayRepository in ThisBuild).value,
    bintrayPackage := (bintrayPackage in ThisBuild).value
  )

// sbt plugin that calls the app on the side
lazy val sbtplugin = (project in file("plugin"))
  .settings(
    name := "sbt-rewritedemo",
    sbtPlugin := true,
    publishMavenStyle := false,
    scalaVersion := "2.10.6",
    crossScalaVersions := Seq("2.10.6"),
    addSbtPlugin("com.eed3si9n" % "sbt-sidedish" % "0.1.0"),
    bintrayRepository := (bintrayRepository in ThisBuild).value,
    bintrayPackage := (bintrayPackage in ThisBuild).value
  )
