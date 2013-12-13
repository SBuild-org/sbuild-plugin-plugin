package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild.TargetRefs
import de.tototec.sbuild.Project

class SBuildPlugin(val name: String)(implicit project: Project) {
  var packageName: Option[String] = None
  var pluginClass: String = _
  var pluginFactoryClass: String = _
  var pluginVersion: String = "0.0.0"
  var sbuildVersion: String = _
  // TODO: support raw deps
  var deps: Seq[String] = Seq()
  var testDeps: Option[TargetRefs] = None
  var exportedPackages: Option[Seq[String]] = None
}