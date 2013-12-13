package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild.TargetRefs
import de.tototec.sbuild.Project

/**
 * Plugin instance class used for configuration of the SBuild Plugin Plugin.
 */
class SBuildPlugin(val name: String)(implicit project: Project) {

  /**
   * The base name of the build JAR. If `[[scala.None$]]`, it will be derived from the `[[SBuildPlugin#pluginClass]]` property.
   */
  var packageName: Option[String] = None

  /**
   * The fully qualified plugin instance class name.
   * This class needs to be exported to be available by a plugin consumer
   * (which is the case if you do not touch `[[SBuildPlugin#exportPackages]]`.
   *
   * This property contributes to the manifest entry `SBuild-Plugin`.
   */
  var pluginClass: String = _

  /**
   * The fully qualified plugin factory class name, which must implement the `[[de.tototec.sbuild.Plugin]]` trait.
   * This class is not required to be in an exported packages.
   *
   * This property contributes to the manifest entry `SBuild-Plugin`.
   */
  var pluginFactoryClass: String = _

  /**
   * The plugin version.
   *
   * This property contributes to the manifest entry `SBuild-Plugin`.
   */
  var pluginVersion: String = "0.0.0"

  /**
   * The SBuild version.
   * This version is used to compile the plugin.
   * This will also be the lowest compatible version required to run this plugin.
   * 
   * This property contributes to the manifest entry 'SBuild-Version`.
   */
  var sbuildVersion: String = _

  /**
   * Additional dependencies of this plugin.
   * You can use the `"raw:"` when you do now want a dependency treated as a plugin,
   * in which case its manifest will not be searched for SBuild-secific entries.
   * 
   * This property contributes to the manifest entry `SBuild-PluginClasspath`. 
   */
  var deps: Seq[String] = Seq()

  /**
   * Packages this plugin should export.
   * If `[[scala.None$]]`, this plugin just exports the package containing the plugin class (`[[SBuildPlugin#pluginClass]]`).
   * If you configured additional dependencies with `[[SBuildPlugin#deps]]` and want to make them available to the plugin consumer,
   * you have to add the contained packages to this list.
   * 
   * This property contributes to the manifest entry `SBuild-ExportPackage`.
   */
  var exportedPackages: Option[Seq[String]] = None

  // var testDeps: Option[TargetRefs] = None
}