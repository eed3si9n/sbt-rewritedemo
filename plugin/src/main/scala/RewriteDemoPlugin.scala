package sbtrewritedemo

import sbt._
import Keys._
import sbtsidedish.Sidedish

object RewriteDemoPlugin extends AutoPlugin {
  override def requires = sbt.plugins.JvmPlugin

  object autoImport extends RewriteDemoKeys
  import autoImport._

  val sidedish = Sidedish("sbtrewritedemo-metatool",
    file("sbtrewritedemo-metatool"),
    // scalaVersion
    "2.12.1",
    // ModuleID of your app
    List("com.eed3si9n" %% "rewritedemo" % "0.1.0"),
    // main class
    "sbtrewritedemo.RewriteApp")

  override def extraProjects: Seq[Project] =
    List(sidedish.project)

  override def projectSettings = Seq(
    rewritedemoOrigin := "example",
    sourceGenerators in Compile +=
      Def.sequential(
        Def.taskDyn {
          val example = LocalProject(rewritedemoOrigin.value)
          val workingDir = baseDirectory.value
          val out = (sourceManaged in Compile).value / "rewritedemo"
          Def.taskDyn {
            val srcDirs = (sourceDirectories in (example, Compile)).value
            val srcs = (sources in (example, Compile)).value
            val cp = (fullClasspath in (example, Compile)).value
            val jvmOptions = List("-Dscalameta.sourcepath=" + "\"" + srcDirs.mkString(java.io.File.pathSeparator) + "\"",
              "-Dscalameta.classpath=" + "\"" + cp.mkString(java.io.File.pathSeparator)+ "\"",
              "-Drewrite.out=" + out)
            Def.task {
              sidedish.forkRunTask(workingDir, jvmOptions = jvmOptions, args = Nil).value
            }
          }
        },
        Def.task {
          val out = (sourceManaged in Compile).value / "rewritedemo"
          (out ** "*.scala").get
        }
      ).taskValue
  )
}

trait RewriteDemoKeys {
  val rewritedemoOrigin = settingKey[String]("")
}

object RewriteDemoKeys extends RewriteDemoKeys
