package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild._

trait SBuildVersion {
  def version: String
  def sbuildClasspath: TargetRefs
  def scalaClasspath: TargetRefs
  def scalaCompilerClasspath: TargetRefs
  def scalaTestClasspath: TargetRefs
}

object SBuildVersion {

  trait Scala_2_10_3 extends SBuildVersion {
    implicit protected def project: Project
    override val scalaClasspath: TargetRefs =
      "mvn:org.scala-lang:scala-library:2.10.3" ~
        "mvn:org.scala-lang:scala-reflect:2.10.3"
    override val scalaCompilerClasspath: TargetRefs =
      "mvn:org.scala-lang:scala-library:2.10.3" ~
        "mvn:org.scala-lang:scala-reflect:2.10.3" ~
        "mvn:org.scala-lang:scala-compiler:2.10.3"
  }

  trait ScalaTest_2_0 extends SBuildVersion {
    implicit protected def project: Project
    override val scalaTestClasspath: TargetRefs = "mvn:org.scalatest:scalatest_2.10:2.0"
  }

  private[this] val baseUrl = "http://sbuild.tototec.de/sbuild/attachments/download"

  def v0_7_0(implicit _project: Project) = new SBuildVersion with Scala_2_10_3 with ScalaTest_2_0 {
    override protected val project = _project
    override val version: String = "0.7.0"
    override val sbuildClasspath: TargetRefs =
      s"${baseUrl}/83/de.tototec.sbuild-0.7.0.jar" ~
        s"${baseUrl}/85/de.tototec.sbuild.addons-0.7.0.jar" ~
        s"${baseUrl}/86/de.tototec.sbuild.ant-0.7.0.jar"
  }

  def v0_7_1(implicit _project: Project) = new SBuildVersion with Scala_2_10_3 with ScalaTest_2_0 {
    override protected val project = _project
    override val version: String = "0.7.1"
    override val sbuildClasspath: TargetRefs =
      s"${baseUrl}/88/de.tototec.sbuild-0.7.1.jar" ~
        s"${baseUrl}/90/de.tototec.sbuild.addons-0.7.1.jar" ~
        s"${baseUrl}/91/de.tototec.sbuild.ant-0.7.1.jar"
  }

}
