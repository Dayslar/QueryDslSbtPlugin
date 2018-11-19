import sbt.Keys.{managedClasspath, _}
import sbt.plugins.JvmPlugin
import sbt.{Configuration, Fork, _}

object QueryDSLPlugin extends AutoPlugin {

  val QueryDSL = config("querydsl").hide

  object autoImport {
    val queryDSLVersion = SettingKey[String]("querydsl-version", "QueryDSL version.")
    val queryDSLPackage = SettingKey[String]("querydsl-package", "QueryDSL package to scan.")
  }

  private def compileModels(javaHome: ForkOptions,
                            classpath: Classpath,
                            javaSourceDirectory: File,
                            generatedSourcesDirectory: File,
                            packageToScan: String,
                            streams: TaskStreams) = {
    val cached = FileFunction.cached(streams.cacheDirectory / "querydls", FileInfo.lastModified, FileInfo.exists) {
      in: Set[File] => {
        val outputDirectory: File = generatedSourcesDirectory / "querydsl"
        outputDirectory.mkdirs()

        val runClasspath = classpath.files mkString ":"
        val processor = "com.querydsl.apt.jpa.JPAAnnotationProcessor"
        val classesToProcess = in.toSeq.map(_.getPath)

        Fork.javac(
          javaHome.withJavaHome(sys.env.get("JAVA_HOME") map file),
          Seq("-cp", runClasspath, "-proc:only", "-processor", processor, "-d", outputDirectory.getAbsolutePath) ++ classesToProcess
        )

        (outputDirectory ** "Q*.java").get.toSet

        streams.log("QueryDSLPlugin").debug("Going to process the following files for annotation scanning : " + in.map(_.getPath).mkString(","))
        (generatedSourcesDirectory ** "Q*.java").get.toSet
      }
    }
    cached((javaSourceDirectory / packageToScan ** "*.java").get.toSet)
  }

  private val QueryDSLTemplates = (javaHome: ForkOptions,
                                   dependencyClassPath: Classpath,
                                   pluginClassPath: Classpath,
                                   javaSourceDirectory: File,
                                   generatedDir: File,
                                   packageToScan: String,
                                   streams: TaskStreams) => {
    compileModels(javaHome, dependencyClassPath ++ pluginClassPath, javaSourceDirectory, generatedDir, packageToScan, streams)
    (generatedDir ** "Q*.java").get.map(_.getAbsoluteFile)
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq[Def.Setting[_]](
    queryDSLVersion := "4.2.1",
    libraryDependencies ++= Seq(
      "com.querydsl" % "querydsl-apt" % (QueryDSL / queryDSLVersion).value % QueryDSL.name,
      "com.querydsl" % "querydsl-jpa" % (QueryDSL / queryDSLVersion).value
    ),
    queryDSLPackage := "models",
    managedClasspath in QueryDSL := Classpaths.managedJars(QueryDSL, classpathTypes.value, update.value),

    Compile / sourceGenerators += Def.task {
        QueryDSLTemplates(
          (Compile / forkOptions).value,
          (Compile / dependencyClasspath).value,
          (QueryDSL / managedClasspath).value,
          (Compile / sourceDirectory).value,
          (Compile / sourceManaged).value,
          queryDSLPackage.value,
          streams.value,
        )
    }.taskValue
  )

  override def projectConfigurations: Seq[Configuration] = Seq(QueryDSL)

  override def requires = JvmPlugin
}
