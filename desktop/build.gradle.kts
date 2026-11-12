import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
}
apply(from = "../gradle/versions.gradle.kts")
val roomVersion: String by extra
val sqliteVersion: String by extra
val serializationVersion: String by extra
val coroutinesVersion: String by extra

kotlin {
    jvmToolchain(17)
    sourceSets["main"].kotlin.srcDirs("src/main/kotlin", "../shared/src")
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.foundation)

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.sqlite:sqlite-bundled:$sqliteVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
}

compose.desktop {
    application {
        mainClass = "tm.sowdahasap.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "SowdaHasap"
            packageVersion = "1.1.0"
            description = "Söwda Hasap"
            vendor = "Sowda Hasap"
            modules("java.sql", "jdk.httpserver", "jdk.crypto.ec", "java.naming", "jdk.unsupported")
            windows {
                menu = true
                shortcut = true
                perUserInstall = true
                dirChooser = true
                menuGroup = "Söwda Hasap"
                upgradeUuid = "6f1c2d4e-8a3b-4c5d-9e7f-1a2b3c4d5e6f"
            }
        }
    }
}
