import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / organization     := "org.dijksterhuis"
ThisBuild / organizationName := "dijksterhuis"
ThisBuild / description      := "Template for Python projects built with sbt."
ThisBuild / licenses         := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage         := Some(url("https://github.com/dijksterhuis/sbtPython"))
releaseUseGlobalVersion := false
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/dijksterhuis/sbtPython"),
    "scm:git@github.com:dijksterhuis/sbtPython.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "dijksterhuis",
    name  = "Mike Robeson",
    email = "michael.l.robeson@gmail.com",
    url   = url("https://github.io/dijksterhuis")
  )
)

// -- root project settings
lazy val root = (project in file("."))
  .settings(
    name := "sbtPython",
    libraryDependencies += scalaTest % Test
  )

// -- Plugins
enablePlugins(GitVersioning)
enablePlugins(sbtdocker.DockerPlugin)
enablePlugins(GitBranchPrompt)

// -- python packaging
// TODO

// -- docker
buildOptions in docker := BuildOptions(
  cache = true,
  removeIntermediateContainers = BuildOptions.Remove.Always,
  pullBaseImage = BuildOptions.Pull.Always
)

imageNames in docker := Seq(
  ImageName(
    namespace = Some(organizationName.value),
    repository = name.value.toLowerCase,
    tag = Some("latest")
  ),
  ImageName(
    namespace = Some(organizationName.value),
    repository = name.value.toLowerCase,
    tag = Some("v" + version.value)
  )
)

dockerfile in docker := {
  // -- TODO - Copy over staging files inside specified directories
  // https://github.com/marcuslonnberg/sbt-docker/blob/master/src/main/scala/sbtdocker/Instructions.scala#L314
  val baseDir = baseDirectory.value
  val appDir = baseDir + "/main/python"
  val targetDir = "/app"
  
  new Dockerfile {
    from("python:3")
    // -- TODO - see above w.r.t. copying files from staging
    //copyRaw(appDir, targetDir)
    cmd("echo", "\"hello world!\"")
  }
}