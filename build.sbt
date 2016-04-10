
val inlining = project.in(file("inlining"))
  //.enablePlugins(WriteOutputToFile)
  .enablePlugins(JmhPlugin)
  .settings(
    //scalaVersion := "2.11.8",
    //scalacOptions ++= Seq("-optimise", "-Yinline-warnings", "-unchecked"),

    scalaVersion := "2.12.0-M4",
    scalacOptions ++= Seq("-optimise", "-Yopt-warnings", "-unchecked", "-deprecation"),

    fork := true,

    //javaOptions ++= Seq("-XX:+UnlockDiagnosticVMOptions", "-XX:+TraceClassLoading", "-XX:+LogCompilation", "-XX:LogFile=/Users/chris/code/scala-days-inline-specialized/inlining/hotspot.log", "-XX:+PrintAssembly", "-XX:-BackgroundCompilation")

    // Uncomment this line to disable HotSpot inlining
    javaOptions in Jmh ++= Seq("-XX:+UnlockDiagnosticVMOptions", "-XX:-Inline")
  )

val specialisation = project.in(file("specialisation"))
  .settings(
    scalaVersion := "2.11.8"
  )
