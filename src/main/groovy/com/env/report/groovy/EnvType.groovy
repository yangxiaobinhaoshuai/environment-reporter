package com.env.report.groovy

/**
 * Created by handsomeyang on 2019-04-06.
 *
 * @author handsomeyang
 */
enum EnvType {
  // tool
  OS,
  GIT,
  JAVA,
  GRADLE,
  ANDROID_GRADLE_PLUGIN,
  BUILD_TOOLS,
  COMPILE_SDK_VERSION,
  GRADLE_PROPERTIES,

  // buildInfo
  TASK_INFO,
  INSTANT_RUN,
  DEFAULT_CONFIG_PRODUCT_FLAVOR,
  // The variant apk info ran on the device.
  INSTALLED_VARIANT_VIA_STUDIO,
}
