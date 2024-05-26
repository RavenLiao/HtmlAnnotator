pluginManagement {
    repositories {
        mavenCentral()
        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
        maven { setUrl("https://maven.aliyun.com/repository/google") }  // google
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven/") }    // central、google、jcenter
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
        maven { setUrl("https://maven.aliyun.com/repository/google") }  // google
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven/") }    // central、google、jcenter
        google()
        mavenCentral()
    }
}

rootProject.name = "HtmlAnnotator"
include(":app")
include(":htmlAnnotator-core")
include(":htmlAnnotator-compose")
include(":htmlAnnotator-compose-ext")
include(":htmlAnnotator-view")
