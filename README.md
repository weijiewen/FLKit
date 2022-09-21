# FLKit 

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
	implementation 'com.github.weijiewen:FLKit:2.8'
}
```