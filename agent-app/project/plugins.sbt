// scalastyle:off

// TypeSafe repo
resolvers += Resolver.typesafeRepo("releases")

// Eclipse plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")

// Coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

// Checkstyle
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
