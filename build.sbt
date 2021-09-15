lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.cmhh",
      scalaVersion := "2.13.6",
      version      := "0.1.0"
    )),
    name := "infoshare",
    libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
    libraryDependencies += "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.141.59",

    scalacOptions += "-deprecation",

    assembly / mainClass := Some("org.cmhh.Main"),
    assembly / assemblyJarName := "infoshare.jar",
    
    ThisBuild / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
  )
