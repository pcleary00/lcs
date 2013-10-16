name := "lcs"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "com.googlecode.concurrent-trees" % "concurrent-trees" % "2.2.0"
)     

play.Project.playScalaSettings
