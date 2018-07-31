import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

name := "getting-tweets-agent"
version := "0.1-SNAPSHOT"

scalaVersion := "2.12.6"

lazy val root = project in file(".")


lazy val compileScalastyle = taskKey[Unit]("Run scalastyle before compile")
scalastyleFailOnError := true
compileScalastyle := scalastyle.in(Compile).toTask("").value
compileScalastyle in Compile := (compileScalastyle in Compile).dependsOn(SbtScalariform.autoImport.scalariformFormat in Compile).value
compile in Compile := (compile in Compile).dependsOn(compileScalastyle in Compile).value

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DoubleIndentMethodDeclaration, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(SpacesAroundMultiImports, true)

coverageMinimum := 90
coverageFailOnMinimum := true

libraryDependencies ++= {
  val playWSVersion = "1.1.3"
  Seq(
    "com.typesafe" % "config" % "1.3.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.play" %% "play-ahc-ws-standalone" % playWSVersion,
    "com.typesafe.play" %% "play-ws-standalone-json" % playWSVersion,
    "org.mockito" % "mockito-core" % "2.13.0" % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test
  )
}

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

