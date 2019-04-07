package com.env.report.groovy

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.builder.core.AndroidBuilder
import com.android.builder.model.AndroidProject
import com.android.builder.model.OptionalCompilationStep
import com.android.builder.model.Version
import com.android.builder.signing.DefaultSigningConfig
import com.env.report.groovy.utils.AnsiUtils
import com.env.report.java.EnvEntry
import com.jakewharton.fliptables.FlipTable
import com.jakewharton.fliptables.FlipTableConverters
import org.gradle.StartParameter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException

import static com.env.report.groovy.EnvType.*

/**
 * Created by handsomeyang on 2019-04-05.
 *
 * @author handsomeyang
 */
class EnvReporterPlugin implements Plugin<Project> {

  private Project mProject
  private AppExtension mAppExtension

  private Map<String, String> mTableRow = [:]
  private List<EnvEntry> mEnvEntries = []
  private StartParameter mStartParameter

  @Override
  void apply(Project target) {
    mProject = target
    mStartParameter = mProject.gradle.startParameter

    EnvReporterExtension extension = mProject.extensions.create("envReport",
        EnvReporterExtension)

    mProject.afterEvaluate {

      if (!extension.isReport()) return

      checkForApplication()

      reportEnvironmentInfo()
      reportProjectInfo()

      printTable()
    }
  }

  /**
   * This Plugin ONLY works for Android Application.*/
  private void checkForApplication() {
    AppExtension appExtension = mProject.extensions.getByType(AppExtension.class)
    if (appExtension) {
      mAppExtension = appExtension
    } else {
      throw new PluginApplicationException("env-reporter",
          new Throwable("'Env-reporter' plugin can ONLY apply to Android Application."))
    }
  }

  private void reportEnvironmentInfo() {
    String gradleVersion = mProject.gradle.gradleVersion

    Properties SystemProps = System.properties

    String javaVersion = SystemProps["java.version"] + " (" +
        SystemProps["java.vendor"] +
        ")" +
        "\n" +
        SystemProps['java.home']

    String osInfo = SystemProps['os.name'] + " (" +
        SystemProps['os.version'] +
        " " +
        SystemProps['os.arch'] +
        ")"

    String gitVersion = getToolVersionViaCmd('git', '--version', mProject.rootDir.absolutePath)

    mTableRow += [(format(OS)): osInfo]
    mTableRow += [(format(GRADLE)): gradleVersion]
    mTableRow += [(format(ANDROID_GRADLE_PLUGIN)): Version.ANDROID_GRADLE_PLUGIN_VERSION]
    mTableRow += [(format(JAVA)): javaVersion]
    mTableRow += [(format(GIT)): gitVersion]
  }

  private String getToolVersionViaCmd(String cmd, String[] cmdArgs) {
    String gitVersion
    new ByteArrayOutputStream().withStream { OutputStream os ->
      try {
        mProject.exec {
          executable cmd
          args cmdArgs
          standardOutput = os
        }
      } catch (e) {
        os.write("unspecified".bytes)
        println AnsiUtils.yellow("Can't execute command : '$cmd$cmdArgs' :$e")
      }
      gitVersion = os.toString()
    }
  }

  private void reportProjectInfo() {
    mTableRow += [(format(BUILD_TOOLS)): AndroidBuilder.DEFAULT_BUILD_TOOLS_REVISION.toString()]
    mTableRow += [(format(COMPILE_SDK_VERSION)): mProject.properties['compileSdk'].toString()]

    boolean hasCompilation = mProject.hasProperty(
        AndroidProject.PROPERTY_OPTIONAL_COMPILATION_STEPS)

    boolean isInstantRun = false

    if (hasCompilation) {
      // Comma-separated list
      String options = mProject.property(AndroidProject.PROPERTY_OPTIONAL_COMPILATION_STEPS)
      isInstantRun = options.contains(OptionalCompilationStep.INSTANT_DEV.toString())
    }

    mTableRow += [(format(INSTANT_RUN)): isInstantRun.toString()]
  }

  private void printTable() {
    mTableRow.each { String type, String version -> mEnvEntries += new EnvEntry(type, version)
    }

    // from IDE
    boolean invokedFromIDE = mProject.hasProperty(AndroidProject.PROPERTY_INVOKED_FROM_IDE)

    boolean isAssemble = false
    mStartParameter.taskNames.each { taskName ->
      if (taskName.contains('assemble')) {
        isAssemble = true
      }
    }

    // Click the run button from IDE.
    if (invokedFromIDE && isAssemble && installedVariantInfo) {
      // show select variant info
      mEnvEntries += new EnvEntry(format(INSTALLED_VARIANT_VIA_STUDIO), installedVariantInfo)
    } else {
      // show default config product flavor
      mEnvEntries += new EnvEntry(format(DEFAULT_CONFIG_PRODUCT_FLAVOR), defaultConfigFlavorInfo)
    }
    // show build info
    mEnvEntries += new EnvEntry(format(TASK_INFO), buildInfo)
    println(FlipTableConverters.fromIterable(mEnvEntries, EnvEntry.class))
  }

  private String getInstalledVariantInfo() {
    // ::app:assembleFasterBuildOnApi21Debug
    String startTaskName = mStartParameter.taskNames[0]

    println(" start Task name :$startTaskName")
    // not assemble task
    if (!startTaskName.contains('assemble')) return

    int assembleIndex = startTaskName.indexOf('assemble')

    // FasterBuildOnApi21Debug -> fasterBuildOnApi21Debug
    String variantName = startTaskName.substring(assembleIndex + 'assemble'.length()).uncapitalize()
    println("-----------> $variantName")

    String variantInfo = ""
    mAppExtension.applicationVariants.all { ApplicationVariant variant ->
      if (variant.name == variantName) {

        boolean isAndroidDebugKeystore = false

        variant.signingConfig.with {
          boolean isDefaultAlias = it.keyAlias == DefaultSigningConfig.DEFAULT_ALIAS
          boolean isDefaultPwd = it.keyPassword == DefaultSigningConfig.DEFAULT_PASSWORD
          if (isDefaultAlias && isDefaultPwd) {
            isAndroidDebugKeystore = true
          }
        }

        String[] headers = ['variantName', 'targetSDKVersion', 'minSDKVersion', 'isIDESigningConfig', 'apkName']

        String[][] info = [[variantName,
                            variant.mergedFlavor.targetSdkVersion.apiString,
                            variant.mergedFlavor.minSdkVersion.apiString,
                            isAndroidDebugKeystore.toString(),
                            variant.outputs[0].outputFile.name]]

        variantInfo = FlipTable.of(headers, info)
      }
    }
    return variantInfo
  }

  private String getDefaultConfigFlavorInfo() {
    DefaultConfig defaultConfig = mAppExtension.defaultConfig
    String defaultTargetSdkVersion = defaultConfig.targetSdkVersion.apiString
    String defaultMinSdkVersion = defaultConfig.minSdkVersion.apiString

    String[] headers = ['targetSdkVersion', 'minSdkVersion']
    String[][] info = [[defaultTargetSdkVersion, defaultMinSdkVersion]]
    return FlipTable.of(headers, info)
  }

  private String getBuildInfo() {
    // BuildInfo
    List<String> taskNames = mStartParameter.taskNames
    Map<String, String> projectProperties = mStartParameter.projectProperties
    Map<String, String> systemArgs = mStartParameter.systemPropertiesArgs

    Properties gradleProperties = new Properties()
    gradleProperties.load(mProject.rootProject.file(Project.GRADLE_PROPERTIES).newDataInputStream())

    String[] headers = ['Tasks', '-D', '-P', 'gradle.properties']

    String[][] info = [[taskNames.join(","),
                        systemArgs.toString().replace(",", ",\n"),
                        projectProperties.toString().replace(",", ",\n"),
                        gradleProperties.toString().replace(",", ",\n")]]

    String buildInfo = FlipTable.of(headers, info)
    return buildInfo
  }

  private static String format(Enum anEnum) {
    String formatted = ""
    anEnum.toString().
        split("_")?.each { String part -> formatted += part.toLowerCase().capitalize() + " "
    }
    return formatted
  }
}
