# FLKit iOS转android使用的UI库，仿tableView, 仿alert，loading加载、提示文字、进度封装

## 添加到setting.gradle

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }      //添加
    }
}
```

## 添加项目依赖
```
dependencies {
	implementation 'com.github.weijiewen:FLKit:1.1'
}
```