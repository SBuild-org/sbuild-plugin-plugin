package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild.TargetRefs
import de.tototec.sbuild.ProjectConfigurationException
import de.tototec.sbuild.Project
import de.tototec.sbuild.Plugin
import de.tototec.sbuild.ant.AntFileSet
import de.tototec.sbuild.ant.tasks.AntJar

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

class SBuildPluginPlugin(implicit project: Project) extends Plugin[SBuildPlugin] {
  override def create(name: String): SBuildPlugin = new SBuildPlugin(name)
  override def applyToProject(instances: Seq[(String, SBuildPlugin)]): Unit =
    if (instances.size > 1) throw new ProjectConfigurationException(s"Currently only on configured plugin instance for plugin ${classOf[SBuildPlugin].getName} supported.")
    else instances foreach {
      case (name, plugin) =>
        // Some checks
        if (plugin.pluginClass == null) throw new ProjectConfigurationException(s"The 'pluginClass' configuration parameter was not set for plugin ${classOf[SBuildPlugin].getName}.")
        if (plugin.pluginFactoryClass == null) throw new ProjectConfigurationException(s"The 'pluginFactoryClass' configuration parameter was not set for plugin ${classOf[SBuildPlugin].getName}.")
        if (plugin.sbuildVersion == null) throw new ProjectConfigurationException(s"The 'pluginVersion' configuration parameter was not set for plugin ${classOf[SBuildPlugin].getName}.")

        import de.tototec.sbuild._

        val compilerCp = VersionedArtifacts.scalaCompilerClasspath(plugin.sbuildVersion).map(TargetRef(_))
        val compileCp =
          VersionedArtifacts.scalaClasspath(plugin.sbuildVersion).map(TargetRef(_)) ~
            VersionedArtifacts.sbuildClasspath(plugin.sbuildVersion).map(TargetRef(_)) ~
            plugin.deps.map(TargetRef(_))
        val sources = "scan:src/main/scala;regex=.*\\.scala"
        val resourcesDir = "src/main/resources"
        val resources = s"scan:$resourcesDir"
        val classesDir = Path("target/classes")
        val pluginPackage = plugin.pluginClass.split("\\.").reverse.tail.reverse.mkString(".")
        val packageName = plugin.packageName match {
          case Some(packageName) => packageName
          case None => pluginPackage
        }
        val jar = Path("target") / s"$packageName-${plugin.pluginVersion}.jar"

        Target("phony:clean").evictCache exec {
          Path("target").deleteRecursive
        }

        // TODO: Compile target
        val compileT = Target("phony:compile").cacheable dependsOn compilerCp ~ compileCp ~~ sources exec { ctx: TargetContext =>
          addons.scala.Scalac(
            compilerClasspath = compilerCp.files,
            classpath = compileCp.files,
            destDir = classesDir,
            sources = sources.files,
            deprecation = true,
            debugInfo = "vars",
            fork = true
          )
          classesDir.listFilesRecursive.map(f => ctx attachFile f)
        }

        // TODO: A target that detects the plugin class by analyzing the factory
        val jarT = Target(jar) dependsOn compileT ~ resources exec { ctx: TargetContext =>
          AntJar(
            destFile = ctx.targetFile.get,
            baseDir = classesDir,
            fileSets = if (resources.files.isEmpty) Seq() else Seq(AntFileSet(dir = Path("src/main/resources"))),
            manifestEntries = Map(
              Constants.SBuildPlugin -> s"""${plugin.pluginClass}=${plugin.pluginFactoryClass};version="${plugin.pluginVersion}"""",
              "SBuild-Version" -> plugin.sbuildVersion,
              Constants.SBuildPluginExportPackage -> (plugin.exportedPackages match {
                case Some(p) => p.mkString(",")
                case None => pluginPackage
              }),
              Constants.SBuildPluginClasspath -> plugin.deps.mkString(",")
            )
          )
        }

        Target("phony:jar") dependsOn jarT

        // TODO: source jar target

        plugin.testDeps map { testDeps =>
          // Compile test target
          // run test target
        }
    }

}