name := "scala-adventures"

organization := "com.rea"

version := "0.0.1"

resolvers ++= Seq(
  "rea nexus release" at "http://nexus.delivery.realestate.com.au/nexus/content/repositories/releases",
  Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
  "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")


scalaVersion := "2.11.8"

sbtVersion := "0.13.11"

val specs2Version = "3.8.5"

val catsVersion = "0.7.2"

libraryDependencies ++= Seq(
  "org.typelevel"                   %% "cats"                        % catsVersion,
  "org.scalaz"                      %% "scalaz-core"                 % "7.2.6",
  "io.argonaut"                     %% "argonaut"                    % "6.1",
  "org.scalaz"                      %% "scalaz-core"                 % "7.2.6"          % "test",
  "org.specs2"                      %% "specs2-core"                 % specs2Version    % "test",
  "org.specs2"                      %% "specs2-matcher-extra"        % specs2Version    % "test",
  "org.specs2"                      %% "specs2-cats"                 % specs2Version    % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

// Read here for optional dependencies:
// http://etorreborre.github.io/specs2/guide/org.specs2.guide.Runners.html#Dependencies

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

initialCommands := "import com.rea.scalaadventures._"
