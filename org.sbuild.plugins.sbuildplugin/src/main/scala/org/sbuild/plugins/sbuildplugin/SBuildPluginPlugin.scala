package org.sbuild.plugins.sbuildplugin

import de.tototec.sbuild._
import de.tototec.sbuild.ant.AntFileSet
import de.tototec.sbuild.ant.tasks.AntJar

class SBuildPluginPlugin(implicit project: Project) extends Plugin[SBuildPlugin] {
  override def create(name: String): SBuildPlugin = new SBuildPlugin()
  override def applyToProject(instances: Seq[(String, SBuildPlugin)]): Unit =
    if (instances.size > 1) throw new ProjectConfigurationException(s"Currently only one configured plugin instance for plugin ${classOf[SBuildPlugin].getName} supported.")
    else instances foreach {
      case (name, plugin) =>
        // Some checks
        if (plugin.pluginClass == null) throw new ProjectConfigurationException(s"The 'pluginClass' property was not set for plugin ${classOf[SBuildPlugin].getName}.")
        if (plugin.sbuildVersion == null) throw new ProjectConfigurationException(s"The 'sbuildVersion' property was not set for plugin ${classOf[SBuildPlugin].getName}.")

        val pluginFactoryClass = plugin.pluginFactoryClass match {
          case Some(x) => x
          case None => plugin.pluginClass + "Plugin"
        }

        val compilerCp = plugin.sbuildVersion.scalaCompilerClasspath

        val compileCp =
          plugin.sbuildVersion.scalaClasspath ~
            plugin.sbuildVersion.sbuildClasspath ~
            plugin.deps.map { case d if d.startsWith("raw:") => d.substring(4) case d => d }.map(TargetRef(_))

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

        Target("phony:" + (if (name == "") "clean" else s"clean-$name")).evictCache exec {
          Path("target").deleteRecursive
        }

        // TODO: Compile target
        val compileT = Target("phony:" + (if (name == "") "compile" else s"compile-$name")).cacheable dependsOn
          compilerCp ~ compileCp ~~ sources exec { ctx: TargetContext =>
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

        // TODO: detect the plugin class by analyzing the factory if not given

        val jarT = Target(jar) dependsOn compileT ~ resources exec { ctx: TargetContext =>
          AntJar(
            destFile = ctx.targetFile.get,
            baseDir = classesDir,
            fileSets = if (resources.files.isEmpty) Seq() else Seq(AntFileSet(dir = Path("src/main/resources"))),
            manifestEntries = Map(
              Constants.SBuildPlugin -> s"""${plugin.pluginClass}=${pluginFactoryClass};version="${plugin.pluginVersion}"""",
              "SBuild-Version" -> plugin.sbuildVersion.version,
              Constants.SBuildPluginExportPackage -> (plugin.exportedPackages match {
                case Some(p) => p.mkString(",")
                case None => pluginPackage
              }),
              Constants.SBuildPluginClasspath -> plugin.deps.mkString(",")
            ) ++ plugin.manifest
          )
        }

        Target("phony:" + (if (name == "") "jar" else s"jar-$name")) dependsOn jarT

        val testCp = compileCp ~
          plugin.testDeps.map(TargetRef(_)) ~
          jarT

        val testSourceDir = "src/test/scala"
        val testSources = s"scan:$testSourceDir;regex=.*\\.scala"
        val testClassesDir = Path("target/test-classes")

        val scalatestCp = plugin.sbuildVersion.scalaTestClasspath

        // TODO: Compile target
        val testCompileT = Target("phony:" + (if (name == "") "test-compile" else s"test-compile-$name")).cacheable dependsOn
          compilerCp ~ testCp ~ scalatestCp ~~ testSources exec { ctx: TargetContext =>
            addons.scala.Scalac(
              compilerClasspath = compilerCp.files,
              classpath = testCp.files ++ scalatestCp.files,
              destDir = testClassesDir,
              sources = testSources.files,
              deprecation = true,
              debugInfo = "vars",
              fork = true
            )
            classesDir.listFilesRecursive.map(f => ctx attachFile f)
          }

        Target("phony:" + (if (name == "") "test" else s"test-$name")).cacheable dependsOn
          testCompileT ~ testCp ~ scalatestCp ~ s"scan:$testClassesDir" exec {
            addons.scalatest.ScalaTest(
              classpath = testCp.files ++ scalatestCp.files,
              runPath = Seq(testClassesDir.getPath),
              xmlOutputDir = Path("target/test-output"),
              standardOutputSettings = "G"
            )

          }

        ExportDependencies("eclipse.classpath", testCp ~ scalatestCp)

      // TODO: source jar target

      //        plugin.testDeps map { testDeps =>
      //          // Compile test target
      //          // run test target
      //        }
    }

}
