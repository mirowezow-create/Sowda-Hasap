plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "tm.menin.sowdam"
    compileSdk = 35
    defaultConfig {
        applicationId = "tm.menin.sowdam"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    signingConfigs {
        create("main") {
            storeFile = file("../android/sowdahasap.jks")
            storePassword = "sowdahasap2026"
            keyAlias = "sowdahasap"
            keyPassword = "sowdahasap2026"
        }
    }
    buildTypes {
        release { isMinifyEnabled = false; signingConfig = signingConfigs.getByName("main") }
        debug { signingConfig = signingConfigs.getByName("main") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    lint { abortOnError = false; checkReleaseBuilds = false }
}
