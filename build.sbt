name := "scala-adventures"

organization := "com.rea"

version := "0.0.1"


scalaVersion := "2.11.7"

sbtVersion := "0.13.9"

val specs2Version = "3.6.6"

libraryDependencies ++= Seq(
  "org.scalaz"                      %% "scalaz-core"                 % "7.1.2",
  "org.specs2"                      %% "specs2"                      % specs2Version     % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

// Read here for optional dependencies:
// http://etorreborre.github.io/specs2/guide/org.specs2.guide.Runners.html#Dependencies

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

initialCommands := "import com.rea.scalaadventures._"
