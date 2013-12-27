package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild.ProjectConfigurationException
import de.tototec.sbuild.TargetRefs

object VersionedArtifacts {

  private[this] val baseUrl = "http://sbuild.tototec.de/sbuild/attachments/download/"
  private[this] val fileParts = Map(
    "0.7.0" -> Seq(
      "83/de.tototec.sbuild-0.7.0.jar",
      "85/de.tototec.sbuild.addons-0.7.0.jar",
      "86/de.tototec.sbuild.ant-0.7.0.jar"
    ),
    "0.7.1" -> Seq(
      "88/de.tototec.sbuild-0.7.1.jar",
      "90/de.tototec.sbuild.addons-0.7.1.jar",
      "91/de.tototec.sbuild.ant-0.7.1.jar"
    )
  )

  def sbuildClasspath(sbuildVersion: String): Seq[String] = fileParts.get(sbuildVersion) match {
    case Some(files) => files.map(baseUrl + _)
    case None => throw new RuntimeException("Unsupported SBuild Version: " + sbuildVersion + ". Please select on of: " + fileParts.keys.mkString(", "))
  }

  def scalaClasspath(sbuildVersion: String): Seq[String] = Seq(
    "mvn:org.scala-lang:scala-library:2.10.3",
    "mvn:org.scala-lang:scala-reflect:2.10.3"
  )

  def scalaCompilerClasspath(sbuildVersion: String): Seq[String] = Seq(
    "mvn:org.scala-lang:scala-library:2.10.3",
    "mvn:org.scala-lang:scala-reflect:2.10.3",
    "mvn:org.scala-lang:scala-compiler:2.10.3"
  )

  def scalaTestClasspath(sbuildVersion: String): Seq[String] = Seq(
    "mvn:org.scalatest:scalatest_2.10:2.0"    
  )

}
