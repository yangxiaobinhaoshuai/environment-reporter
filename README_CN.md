# (Android) Environment-Reporter
Android 平台用于聚合项目各种 sdk build tools 版本信息的 Gradle 插件。
 
旨在列出工具版本信息，以及 Android 项目的 gradle.properties 文件中的属性键值对。

当你需要远程解决其他人的项目编译失败问题（build break) 的时候，你会想知道别人本地的构建环境信息，用于排错，这就是这个插件的意义。

## 控制台打印信息截图
<img src="screenshots/屏幕快照 2019-04-07 22.34.56.png" width="400"><img src="screenshots/屏幕快照 2019-04-07 22.35.12.png" width="400">

## 使用
在你的应用根目录下的 build.gradle 中
```
apply plugin: 'env-reporter'

envReport{
  // 在 Gradle 的 configuration 期间，对 gradle.properties 文件进行 io 读取
  // 会造成性能损耗，增加配置生命周期执行时间，所以当不需要构建环境信息调试的时候
  // 建议关掉调试开关，以免造成不必要耗时。
  
  
  // 收集构建环境信息以表格形式打印，默认为 false.
  report true
}
```

## 下载
Gradle 方式；

在你的应用根目录下的 build.gradle 中
```
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'yangxiaobin:environment-reporter:0.1.0'
  }
}
```

## 关于

Built by 
> AGP 3.3.2  (compileOnly)

> Gradle 4.10.1


## 感谢
- [FlipTable](https://github.com/JakeWharton/flip-tables)
- [jansi](https://github.com/fusesource/jansi)

## License
[MIT](http://choosealicense.online/licenses/mit/)
