@file:Suppress("UnstableApiUsage")

rootProject.name = "HtmlAnnotator"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }  // google
        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }  // google
        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":htmlAnnotator-core")
include(":htmlAnnotator-compose")
include(":htmlAnnotator-compose-ext")
include(":htmlAnnotator-view")