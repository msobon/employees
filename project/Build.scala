import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "employees"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "commons-httpclient" % "commons-httpclient" % "3.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
