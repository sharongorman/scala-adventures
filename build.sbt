name := "scala-adventures"

organization := "com.rea"

version := "0.0.1"

resolvers ++= Seq(
  "rea nexus release" at "http://nexus.delivery.realestate.com.au/nexus/content/repositories/releases",
  Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
  "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")


scalaVersion := "2.11.7"

sbtVersion := "0.13.9"

val specs2Version = "3.6.6"

libraryDependencies ++= Seq(
  "org.scalaz"                      %% "scalaz-core"                 % "7.1.6",
  "io.argonaut"                     %% "argonaut"                    % "6.1",
"org.specs2"                      %% "specs2-core"                 % specs2Version    % "test",
  "org.specs2"                      %% "specs2-matcher-extra"        % specs2Version    % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

// Read here for optional dependencies:
// http://etorreborre.github.io/specs2/guide/org.specs2.guide.Runners.html#Dependencies

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

initialCommands := "import com.rea.scalaadventures._"
