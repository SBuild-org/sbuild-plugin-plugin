package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild.ProjectConfigurationException
import de.tototec.sbuild.TargetRefs

trait SBuildVersion {
  def version: String
  def sbuildClasspath: Seq[String]
  def scalaClasspath: Seq[String]
  def scalaCompilerClasspath: Seq[String]
  def scalaTestClasspath: Seq[String]
}

object SBuildVersion {

  trait Scala_2_10_3 extends SBuildVersion {
    override val scalaClasspath: Seq[String] = Seq(
      "mvn:org.scala-lang:scala-library:2.10.3",
      "mvn:org.scala-lang:scala-reflect:2.10.3"
    )
    override val scalaCompilerClasspath: Seq[String] = Seq(
      "mvn:org.scala-lang:scala-library:2.10.3",
      "mvn:org.scala-lang:scala-reflect:2.10.3",
      "mvn:org.scala-lang:scala-compiler:2.10.3"
    )
  }

  trait ScalaTest_2_0 extends SBuildVersion {
    override val scalaTestClasspath: Seq[String] = Seq("mvn:org.scalatest:scalatest_2.10:2.0")
  }

  case class Released(
    override val version: String,
    override val sbuildClasspath: Seq[String],
    override val scalaClasspath: Seq[String],
    override val scalaCompilerClasspath: Seq[String],
    override val scalaTestClasspath: Seq[String]) extends SBuildVersion

  private[this] val baseUrl = "http://sbuild.tototec.de/sbuild/attachments/download"

  val v0_7_0 = new SBuildVersion with Scala_2_10_3 with ScalaTest_2_0 {
    override val version: String = "0.7.0"
    override val sbuildClasspath: Seq[String] = Seq(
      s"${baseUrl}/83/de.tototec.sbuild-0.7.0.jar",
      s"${baseUrl}/85/de.tototec.sbuild.addons-0.7.0.jar",
      s"${baseUrl}/86/de.tototec.sbuild.ant-0.7.0.jar"
    )
  }

  val v0_7_1 = new SBuildVersion with Scala_2_10_3 with ScalaTest_2_0 {
    override val version: String = "0.7.1"
    override val sbuildClasspath: Seq[String] = Seq(
      s"${baseUrl}/88/de.tototec.sbuild-0.7.1.jar",
      s"${baseUrl}/90/de.tototec.sbuild.addons-0.7.1.jar",
      s"${baseUrl}/91/de.tototec.sbuild.ant-0.7.1.jar"
    )
  }

}
