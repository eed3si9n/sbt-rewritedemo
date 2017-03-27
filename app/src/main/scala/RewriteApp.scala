package sbtrewritedemo

import scala.meta._
import scalafix._
import scalafix.rewrite.Rewrite
import scalafix.util._
import java.io.File
import sbt.io.syntax._
import sbt.io.{ IO, Path }
import scala.collection.immutable.Seq

object RewriteApp extends App {
  val sourcepath = sys.props("scalameta.sourcepath")
  val cp = sys.props("scalameta.classpath")
  val out = new File(sys.props("rewrite.out"))
  implicit val mirror = Mirror(cp, sourcepath)
  val r = Rewrite[Any] { ctx =>
    Seq(
      TreePatch.AddGlobalImport(importer"scala.collection.immutable.Seq")
    )
  }
  sourcepath.split(File.pathSeparator).toList foreach { p: String =>
    val path0 = new File(p)
    if (path0.exists) {
      val path = path0.getAbsoluteFile
      if (path.isDirectory) {
        val files = (path ** "*.scala").get
        files foreach { x => handleFile(x, path, out) }
      }
      else handleFile(path, path.getParentFile, out)
    }
    else ()
  }
  def handleFile(file: File, base: File, outDir: File): Unit = {
    if (!outDir.exists) {
      IO.createDirectory(outDir)
    }
    val target = Path.rebase(base, outDir)(file).get
    val options = config.ScalafixConfig().copy(rewrites = List(r))
    val fixed = Scalafix.fix(Input.File(file), options)
    fixed match {
      case Fixed.Success(code) =>
        IO.write(target, code)
      case Fixed.Failed(e: Failure.ParseError) =>
        println(e.toString)
      case Fixed.Failed(e) =>
        println(s"Failed to fix $file. Cause: $e")
    }
  }
}
