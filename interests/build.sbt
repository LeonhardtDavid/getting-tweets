import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

name := "getting-tweets-interests"
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
  val slickVersion = "3.2.3"
  val playSlickVersion = "3.0.3"
  Seq(
    guice,
    "mysql" % "mysql-connector-java" % "8.0.11",
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "com.typesafe.play" %% "play-slick" % playSlickVersion,
    "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
    "org.mockito" % "mockito-core" % "2.19.1" % Test
  )
}

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:reflectiveCalls"
)

