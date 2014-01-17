package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild.TargetRefs
import de.tototec.sbuild.Project

/**
 * Plugin instance class used for configuration of the SBuild Plugin Plugin.
 *
 * @param packageName The base name of the built JAR.
 *   If `[[scala.None$]]`, it will be derived from the `[[SBuildPlugin#pluginClass]]` property.
 *
 * @param packageClass The fully qualified plugin instance class name.
 *   This class needs to be exported to be available by a plugin consumer
 *   (which is the case if you do not touch `[[SBuildPlugin#exportPackages]]`.
 *
 *   This property contributes to the manifest entry `SBuild-Plugin`.
 *
 * @þaram pluginFactoryClass The fully qualified plugin factory class name, which must implement the `[[de.tototec.sbuild.Plugin]]` trait.
 *   This class is not required to be in an exported packages.  If `[[scala.None]]`, the value of packageClass plus the suffix `"Plugin"` is used.
 *
 *   This property contributes to the manifest entry `SBuild-Plugin`.
 *
 * @param pluginVersion  The plugin version.
 *
 *   This property contributes to the manifest entry `SBuild-Plugin`.
 *
 * @param sbuildVersion The SBuild version.
 *   This version is used to compile the plugin.
 *   This will also be the lowest compatible version required to run this plugin.
 *
 *   This property contributes to the manifest entry 'SBuild-Version`.
 *
 * @param deps Additional dependencies of this plugin.
 *   You can use the `"raw:"` when you do now want a dependency treated as a plugin,
 *   in which case its manifest will not be searched for SBuild-secific entries.
 *
 *   This property contributes to the manifest entry `SBuild-PluginClasspath`.
 *
 * @param exportedPackages Packages this plugin should export.
 *   If `[[scala.None$]]`, this plugin just exports the package containing the plugin class (`[[SBuildPlugin#pluginClass]]`).
 *   If you configured additional dependencies with `[[SBuildPlugin#deps]]` and want to make them available to the plugin consumer,
 *   you have to add the contained packages to this list.
 *
 *   This property contributes to the manifest entry `SBuild-ExportPackage`.
 *
 *   @param manifest Additional manifest entries added to the plugin JAR file.
 *
 */
case class SBuildPlugin(
  packageName: Option[String] = None,
  pluginClass: String = null,
  pluginFactoryClass: Option[String] = None,
  pluginVersion: String = "0.0.0",
  sbuildVersion: String = null,
  deps: Seq[String] = Seq(),
  testDeps: Seq[String] = Seq(),
  exportedPackages: Option[Seq[String]] = None,
  manifest: Map[String, String] = Map())

  // var testDeps: Option[TargetRefs] = None
