import ReleaseTransformations._
import sbtdocker.DockerPlugin.autoImport._

// release without publishing to a ivy/maven repo

lazy val buildDocker = ReleaseStep(action = st => {
  val extracted = Project.extract(st)
  val ref: ProjectRef = extracted.get(thisProjectRef)
  extracted.runAggregated(
    sbtdocker.DockerKeys.docker in sbtdocker.DockerPlugin.autoImport.docker in ref,
    st)
  st
})

lazy val pushDocker = ReleaseStep(action = st => {
  val extracted = Project.extract(st)
  val ref: ProjectRef = extracted.get(thisProjectRef)
  extracted.runAggregated(
    sbtdocker.DockerKeys.dockerPush in sbtdocker.DockerPlugin.autoImport.docker in ref,
    st)
  st
})

releaseProcessDocker := Seq[ReleaseStep](
  checkSnapshotDependencies,                    // : ReleaseStep
  inquireVersions,                              // : ReleaseStep
  runClean,                                     // : ReleaseStep
  runTest,                                      // : ReleaseStep
  setReleaseVersion,                            // : ReleaseStep
  commitReleaseVersion,                         // : ReleaseStep, performs the initial git checks
  tagRelease,                                   // : ReleaseStep
  buildDocker,                                  // : ReleaseStep, build the docker images
  pushDocker,                                   // : ReleaseStep, push the docker image
  setNextVersion,                               // : ReleaseStep
  commitNextVersion,                            // : ReleaseStep
  pushChanges                                   // : ReleaseStep, also checks that an upstream branch is properly configured
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,                    // : ReleaseStep
  inquireVersions,                              // : ReleaseStep
  runClean,                                     // : ReleaseStep
  runTest,                                      // : ReleaseStep
  setReleaseVersion,                            // : ReleaseStep
  commitReleaseVersion,                         // : ReleaseStep, performs the initial git checks
  tagRelease,                                   // : ReleaseStep
  setNextVersion,                               // : ReleaseStep
  commitNextVersion,                            // : ReleaseStep
  pushChanges                                   // : ReleaseStep, also checks that an upstream branch is properly configured
)