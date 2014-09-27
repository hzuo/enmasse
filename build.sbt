name := "enmasse"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

play.Project.playScalaSettings

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-feature" )

playRunHooks <+= baseDirectory.map(base => Grunt(base / "ui"))


libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1102-jdbc41"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "0.6.1"


