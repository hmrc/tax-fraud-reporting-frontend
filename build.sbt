import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings.{integrationTestSettings, targetJvm}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "tax-fraud-reporting-frontend"

targetJvm := "jvm-1.8"

val silencerVersion = "1.7.3"

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys

  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := List("<empty>",
      "Reverse.*",
      "uk\\.gov\\.hmrc\\.taxfraudreportingfrontend\\.views.*",
      "uk\\.gov\\.hmrc\\.taxfraudreportingfrontend\\.config.*",
      "uk\\.gov\\.hmrc\\.taxfraudreportingfrontend\\.models.audit.*",
      "logger.*\\(.*\\)",
      ".*(AuthService|BuildInfo|Routes|TestOnly).*").mkString(";"),
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

lazy val playSettings: Seq[Setting[_]] = Seq(
  routesImport ++= Seq("uk.gov.hmrc.taxfraudreportingfrontend.domain._"),
  RoutesKeys.routesImport += "uk.gov.hmrc.taxfraudreportingfrontend.models._"
)


lazy val twirlSettings: Seq[Setting[_]] = Seq(
  TwirlKeys.templateImports ++= Seq("uk.gov.hmrc.taxfraudreportingfrontend.views.html._", "uk.gov.hmrc.taxfraudreportingfrontend.domain._")
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := "2.12.13",
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    pipelineStages in Assets := Seq(gzip),
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(scoverageSettings)
  .settings(twirlSettings)
  .settings(playSettings)
  .settings(PlayKeys.playDefaultPort := 8330)
