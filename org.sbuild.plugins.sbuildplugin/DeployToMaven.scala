import de.tototec.sbuild._

@version("0.7.0")
@classpath(
  "mvn:org.apache.ant:ant:1.8.4",
  "../../maven-deploy/org.sbuild.plugins.mavendeploy/target/org.sbuild.plugins.mavendeploy-0.0.9000.jar"
)
class SBuild(implicit _project: Project) {

  val namespace = "org.sbuild.plugins.sbuildplugin"
  val version = "0.1.0"

  import org.sbuild.plugins.mavendeploy._

  Plugin[MavenDeploy] configure { p =>
    p.groupId = "org.sbuild"
    p.artifactId = namespace
    p.version = version

    p.repository = Repository.SonatypeOss
    val scm = "https://github.com/SBuild-org/sbuild-plugin-plugin"
    p.scm = Option(Scm(url = scm, connection = scm))
    p.developers = Seq(Developer(id = "TobiasRoeser", name = "Tobias Roeser", email = "le.petit.fou@web.de"))
    p.gpg = true

    p.files = Map(
      "jar" -> s"target/${namespace}-${version}.jar",
      "sources" -> s"target/${namespace}-${version}-sources.jar",
      "javadoc" -> "target/fake.jar"
    )
  }

  Target("target/fake.jar") dependsOn "LICENSE.txt" exec { ctx: TargetContext =>
    import de.tototec.sbuild.ant._
    tasks.AntJar(destFile = ctx.targetFile.get, fileSet = AntFileSet(file = "LICENSE.txt".files.head))
  }

}
