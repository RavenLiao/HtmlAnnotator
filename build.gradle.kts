// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.mavenpublish) apply false
}

allprojects {
    /**
     * publish config
     */
    if (hasProperty("signing.keyId")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("signing.password")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("signing.secretKeyRingFile")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("mavenCentralUsername")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("mavenCentralPassword")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("versionName")    // configured in the rootProject/gradle.properties file
        && hasProperty("GROUP")    // configured in the rootProject/gradle.properties file
        && hasProperty("POM_ARTIFACT_ID")    // configured in the project/gradle.properties file
    ) {
        apply { plugin("com.vanniktech.maven.publish") }

        configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            version = property("versionName").toString()
        }
    }
}