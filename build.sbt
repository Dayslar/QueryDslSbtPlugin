scalaVersion := "2.12.7"

version := "0.1.1"
organization := "com.dayslar.play"
description := "Sbt plugin for querydsl"
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

libraryDependencies ++= Seq(
  "com.querydsl" % "querydsl-apt" % "4.2.1"
)

lazy val root = (project in file("."))
  .settings(
    sbtPlugin := true,
    name := "QueryDslSbtPlugin",
    publishMavenStyle := false,
    bintrayRepository := "sbt-plugins",
    bintrayOrganization in bintray := None
  )
