plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}
apply(from = "../gradle/versions.gradle.kts")
val roomVersion: String by extra
val sqliteVersion: String by extra
val serializationVersion: String by extra
val coroutinesVersion: String by extra

android {
    namespace = "tm.sowdahasap"
    compileSdk = 35

    defaultConfig {
        applicationId = "tm.sowdahasap"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"
    }

    signingConfigs {
        // Hemişe şol bir gol: täze APK köne APK-nyň üstüne gurnalýar (maglumat ýitmeýär)
        create("main") {
            storeFile = file("sowdahasap.jks")
            storePassword = "sowdahasap2026"
            keyAlias = "sowdahasap"
            keyPassword = "sowdahasap2026"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("main")
        }
        debug {
            signingConfig = signingConfigs.getByName("main")
        }
    }
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java", "../shared/src")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.05.00"))
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.sqlite:sqlite-bundled:$sqliteVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
