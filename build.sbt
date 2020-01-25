import play.sbt.routes.RoutesKeys
RoutesKeys.routesImport := Seq.empty

name := "play-qrway"

version := "4.0.0"

scalaVersion := "2.12.8"

lazy val playSlickVersion = "5.0.0"
lazy val silhouetteVersion = "6.1.1"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-totp" % silhouetteVersion,
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "org.postgresql" % "postgresql" % "9.4.1211",
  "com.github.tminglei" %% "slick-pg" % "0.18.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.18.1",
  "com.iheart" %% "ficus" % "1.4.3",
  "com.typesafe.play" %% "play-slick"               % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions"    % playSlickVersion,
  caffeine,
  guice,
  filters,
  "com.sendgrid" % "sendgrid-java" % "4.4.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)
