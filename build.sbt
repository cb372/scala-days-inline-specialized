
val inlining = project.in(file("inlining"))
  //.enablePlugins(WriteOutputToFile)
  .enablePlugins(JmhPlugin)
  .settings(
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq("-optimise", "-Yinline-warnings", "-unchecked")

    //scalaVersion := "2.12.0-M4",
    //scalacOptions ++= Seq("-Yopt:l:classpath", "-Yopt-warnings", "-unchecked", "-deprecation")

    // Uncomment this line to write the JIT log to a file
    //javaOptions ++= Seq("-XX:+UnlockDiagnosticVMOptions", "-XX:+TraceClassLoading", "-XX:+LogCompilation", "-XX:LogFile=/Users/chris/code/scala-days-inline-specialized/inlining/hotspot.log", "-XX:+PrintAssembly", "-XX:-BackgroundCompilation")

    // Uncomment this line to disable HotSpot inlining
    //javaOptions in Jmh ++= Seq("-XX:+UnlockDiagnosticVMOptions", "-XX:-Inline", "-XX:MaxInlineSize=0", "-XX:MaxTrivialSize=0")
  )

val specialisation = project.in(file("specialisation"))
  .enablePlugins(JmhPlugin)
  .settings(
    scalaVersion := "2.11.8",
    libraryDependencies += "org.spire-math" %% "spire" % "0.11.0",
    libraryDependencies += "org.scala-lang" % "scalap" % scalaVersion.value,
    javaOptions in Jmh ++= Seq("-Xms2g", "-Xmx2g")
  )
