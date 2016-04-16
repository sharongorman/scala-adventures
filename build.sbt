name := "scala-adventures"

organization := "com.rea"

version := "0.0.1"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.4" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

// Read here for optional dependencies:
// http://etorreborre.github.io/specs2/guide/org.specs2.guide.Runners.html#Dependencies

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

initialCommands := "import com.rea.scalaadventures._"
