name := """etas"""
organization := "etas"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += ws
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0"
libraryDependencies += "net.sourceforge.jtds" % "jtds" % "1.3.1"
libraryDependencies += "javax.mail" % "mail" % "1.4.7" withSources()

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "etas.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "etas.binders._"
