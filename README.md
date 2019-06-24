> [中文版 README](README_CN.md)
# (Android) Environment-Reporter
This is a gradle plugin for Android Application to aggregate tools information.
 
 Intending to list the environment toolInfo(e.g. tool version) of the common tools and build properties.

There may be a scene when you troubleshooting in remote or users' gradle build environment via logs. Mostly, you want to see the environment information, that's why this comes out.

## Screenshots
<img src="screenshots/屏幕快照 2019-04-07 22.34.56.png" width="400"><img src="screenshots/屏幕快照 2019-04-07 22.35.12.png" width="400">

## Usage
In your application build.gradle.
```
apply plugin: 'env-reporter'

envReport{
  // Cause the plugin doing a few IO work reading 'gradle.properties' during configuration phase,
  // So, it may be some unexpected traffic when you don't need it.
  // Turn off it, then it will do noting in your build configuration phase.
  
  // Collect the info and print the table, default to false.
  report true
}
```

## Download
Gradle :

In your project root directory build.gradle.
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

## About This Plugin 

Built by 
> AGP 3.3.2  (compileOnly)

> Gradle 4.10.1


## Thanks to
- [FlipTable](https://github.com/JakeWharton/flip-tables)
- [jansi](https://github.com/fusesource/jansi)

## License
[MIT](http://choosealicense.online/licenses/mit/)
