import Dependencies._
import scala.sys.process._

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

// -- Python tests
// https://stackoverflow.com/a/48415997
val testPythonTask = TaskKey[Unit]("testPython", "Run python tests.")
fork in testPythonTask := true
testPythonTask := {
  val s: TaskStreams = streams.value
  val baseDir = baseDirectory.value
  s.log.info("Executing task testPython")
  lazy val result: Int = Process("python3 -m unittest src.test.python.test_main").!
  if(result != 0){sys.error("Python tests failed!")}
}

//attach custom test task to default test tasks
test in Test := {
  testPythonTask.value
  (test in Test).value
}
testOnly in Test := {
  testPythonTask.value
  (testOnly in Test).inputTaskValue
}

// -- python packaging
// TODO
// 1. Zip archiving
// 2. setup.py install?

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
  val username = "python"
  val appDir = "src/main/python"
  val targetDir = "/app"
  val requirementsFile = "requirements.txt"
  
  new Dockerfile {
    from("python:3")
    run("useradd", "-ms", "/bin/bash", username)
    // copy python files to staging area first, then copy to image
    stageFile(file(appDir), targetDir)
    copyRaw(targetDir, targetDir)
    run("chown", "-R", username + ":" + username, targetDir)
    run("chmod", "-R", "+x", targetDir)
    user(username)
    cmd("/app/main.py")
  }
}