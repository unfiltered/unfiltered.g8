organization := "com.example"

name := "$name$"

version := "$version$"

scalaVersion := "$scala_version$"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-directives" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-filter" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-jetty" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-specs2" % "$unfiltered_version$" % "test"
)
