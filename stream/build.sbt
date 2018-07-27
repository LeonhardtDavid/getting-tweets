import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

name := "getting-tweets-stream"
version := "0.1-SNAPSHOT"

scalaVersion := "2.12.6"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

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

play.sbt.routes.RoutesKeys.routesImport ++= Seq(
  "java.time.LocalDateTime",
  "binders.QueryStringBinder._"
)

libraryDependencies ++= {
  val reactivemongoVersion = "0.15.0"
  Seq(
    guice,
    "com.danielasfregola" %% "twitter4s" % "5.5",
    "org.reactivemongo" %% "play2-reactivemongo" % s"$reactivemongoVersion-play26",
    "org.reactivemongo" %% "reactivemongo-akkastream" % reactivemongoVersion,
    "org.mockito" % "mockito-core" % "2.19.1" % Test
  )
}

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:reflectiveCalls"
)

