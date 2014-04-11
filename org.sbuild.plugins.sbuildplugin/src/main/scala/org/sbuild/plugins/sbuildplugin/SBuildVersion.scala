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

  class SBuild_0_7_x(override val version: String)(implicit _project: Project) extends SBuildVersion with Scala_2_10_3 with ScalaTest_2_0 {
    override protected def project = _project
    override val sbuildClasspath: TargetRefs = {
      val baseUrl = s"http://sbuild.org/uploads/${version}"
      s"${baseUrl}/de.tototec.sbuild-${version}.jar" ~
        s"${baseUrl}/de.tototec.sbuild.addons-${version}.jar" ~
        s"${baseUrl}/de.tototec.sbuild.ant-${version}.jar"
    }
  }

  def v0_7_0(implicit _project: Project) = new SBuild_0_7_x("0.7.0")
  def v0_7_1(implicit _project: Project) = new SBuild_0_7_x("0.7.1")
  def v0_7_2(implicit _project: Project) = new SBuild_0_7_x("0.7.2")
  def v0_7_3(implicit _project: Project) = new SBuild_0_7_x("0.7.3")
  def v0_7_4(implicit _project: Project) = new SBuild_0_7_x("0.7.4")
  def v0_7_5(implicit _project: Project) = new SBuild_0_7_x("0.7.5")

  def v0_7_9010(implicit _project: Project) = new SBuildVersion {
    private[this] val scalaVersion = "2.11.0-RC4"
    private[this] val scalaBinVersion = "2.11.0-RC4"

    override val version: String = "0.7.9010.0-8-0-M1"
    override val sbuildClasspath: TargetRefs =
      s"http://sbuild.org/uploads/sbuild/${version}/org.sbuild-${version}.jar"
    override val scalaClasspath: TargetRefs =
      s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
        s"mvn:org.scala-lang:scala-reflect:${scalaVersion}" ~
        s"mvn:org.scala-lang.modules:scala-xml_${scalaBinVersion}:1.0.1"
    override val scalaCompilerClasspath: TargetRefs =
      s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
        s"mvn:org.scala-lang:scala-reflect:${scalaVersion}" ~
        s"mvn:org.scala-lang:scala-compiler:${scalaVersion}"
    override val scalaTestClasspath: TargetRefs =
      s"mvn:org.scalatest:scalatest_${scalaBinVersion}:2.1.3"
  }

}
