import de.tototec.sbuild._
import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._

@version("0.7.1")
@classpath(
  "mvn:org.apache.ant:ant:1.8.4",
  "mvn:org.sbuild:org.sbuild.plugins.mavendeploy:0.1.0"
)
class SBuild(implicit _project: Project) {

  val namespace = "org.sbuild.plugins.sbuildplugin"
  val version = "0.3.0.9000"
  val url = "https://github.com/SBuild-org/sbuild-plugin-plugin"

  val sbuildVersion = "0.7.1"
  val scalaVersion = "2.10.3"
  val jar = s"target/${namespace}-${version}.jar"
  val sourcesJar = s"target/${namespace}-${version}-sources.jar"

  val scalaCompiler = s"mvn:org.scala-lang:scala-compiler:$scalaVersion" ~
    s"mvn:org.scala-lang:scala-library:$scalaVersion" ~
    s"mvn:org.scala-lang:scala-reflect:$scalaVersion"

  val sbuildCore = s"http://sbuild.tototec.de/sbuild/attachments/download/88/de.tototec.sbuild-$sbuildVersion.jar"
  val sbuildAddons = s"http://sbuild.tototec.de/sbuild/attachments/download/90/de.tototec.sbuild.addons-$sbuildVersion.jar"
  val sbuildAnt = s"http://sbuild.tototec.de/sbuild/attachments/download/91/de.tototec.sbuild.ant-$sbuildVersion.jar"

  val pluginCp = Seq("mvn:org.apache.ant:ant:1.8.4")

  val compileCp =
    s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
      sbuildCore ~ sbuildAddons ~ sbuildAnt ~
      pluginCp.map(TargetRef(_))

  ExportDependencies("eclipse.classpath", compileCp)

  Target("phony:all") dependsOn sourcesJar ~ jar

  Target("phony:clean").evictCache exec {
    AntDelete(dir = Path("target"))
  }

  val sources = "scan:src/main/scala"

  Target("phony:compile").cacheable dependsOn scalaCompiler ~ compileCp ~ sources exec {
    val output = "target/classes"
    addons.scala.Scalac(
      compilerClasspath = scalaCompiler.files,
      classpath = compileCp.files,
      sources = sources.files,
      destDir = Path(output),
      unchecked = true, deprecation = true, debugInfo = "vars"
    )
  }

  Target(sourcesJar) dependsOn sources ~ "LICENSE.txt" exec { ctx: TargetContext =>
    AntZip(destFile = ctx.targetFile.get, fileSets = Seq(
      AntFileSet(dir = Path("src/main/scala")),
      AntFileSet(file = Path("LICENSE.txt"))
    ))
  }

  Target("phony:scaladoc").cacheable dependsOn scalaCompiler ~ compileCp ~ sources exec {
    addons.scala.Scaladoc(
      scaladocClasspath = scalaCompiler.files,
      classpath = compileCp.files,
      sources = sources.files,
      destDir = Path("target/scaladoc"),
      deprecation = true, unchecked = true, implicits = true,
      docVersion = sbuildVersion,
      docTitle = s"SBuild Experimental API Reference"
    )
  }

  Target(jar) dependsOn "compile" ~ "LICENSE.txt" exec { ctx: TargetContext =>
    AntJar(
      destFile = ctx.targetFile.get,
      baseDir = Path("target/classes"),
      fileSet = AntFileSet(file = Path("LICENSE.txt")),
      manifestEntries = Map(
        "SBuild-ExportPackage" -> s"$namespace,org.apache.tools.ant.*",
        "SBuild-Plugin" -> s"""${namespace}.SBuildPlugin=${namespace}.SBuildPluginPlugin;version="${version}"""",
        "SBuild-Classpath" -> pluginCp.map("raw:" + _).mkString(","),
        "SBuild-Version" -> "0.7.1"
      )
    )
  }

  import org.sbuild.plugins.mavendeploy._

  Plugin[MavenDeploy] configure { _.copy(
    groupId = "org.sbuild",
    artifactId = namespace,
    version = version,
    artifactName = Some("SBuild SBuild Plugin"),
    description = Some("An SBuild Plugin to easily create SBuild plugins."),
    repository = Repository.SonatypeOss,
    scm = Option(Scm(url = url, connection = url)),
    developers = Seq(Developer(id = "TobiasRoeser", name = "Tobias Roeser", email = "le.petit.fou@web.de")),
    gpg = true,
    licenses = Seq(License.Apache20),
    url = Some(url),
    files = Map(
      "jar" -> s"target/${namespace}-${version}.jar",
      "sources" -> s"target/${namespace}-${version}-sources.jar",
      "javadoc" -> "target/fake.jar"
    )
  )}

  Target("target/fake.jar") dependsOn "LICENSE.txt" exec { ctx: TargetContext =>
    import de.tototec.sbuild.ant._
    tasks.AntJar(destFile = ctx.targetFile.get, fileSet = AntFileSet(file = "LICENSE.txt".files.head))
  }

}
