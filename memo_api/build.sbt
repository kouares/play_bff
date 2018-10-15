name := """memo_api"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

// play default
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// slick
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.2" ,
  "com.typesafe.slick" %% "slick-codegen" % "3.2.1" ,
  "mysql" % "mysql-connector-java" % "5.1.42"
)

// jackson
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.9.2" ,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.9.2"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

import scalariform.formatter.preferences._
// https://github.com/scala-ide/scalariform
// フォーマッタは基本的にScala Style Guideに準拠
scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(SpacesAroundMultiImports, false)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(AlignParameters, false)
  .setPreference(CompactStringConcatenation, false)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(PreserveSpaceBeforeArguments, false)
  .setPreference(RewriteArrowSymbols, false)
  .setPreference(SpaceBeforeColon, false)
  .setPreference(SpaceInsideBrackets, false)
  .setPreference(SpaceInsideParentheses, false)
  .setPreference(SpacesAroundMultiImports, false)
  .setPreference(IndentSpaces, 2)
