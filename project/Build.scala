import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "employees"
    val appVersion      = "1.0"

    val appDependencies = Seq(
       javaCore, javaJdbc, javaEbean,"commons-httpclient" % "commons-httpclient" % "3.1"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )
}	
	
