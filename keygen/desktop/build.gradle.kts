import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
}

kotlin {
    jvmToolchain(17)
    sourceSets["main"].kotlin.srcDirs("src/main/kotlin", "../shared/src")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.foundation)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
}

compose.desktop {
    application {
        mainClass = "tm.sowdahasap.keygen.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "SowdaHasapKeygen"
            packageVersion = "1.0.0"
            description = "Söwda Hasap açar generatory"
            modules("jdk.crypto.ec", "jdk.unsupported")
            windows {
                menu = true
                shortcut = true
                perUserInstall = true
                menuGroup = "Söwda Hasap"
                upgradeUuid = "8a2b3c4d-1e2f-4a5b-8c7d-9e0f1a2b3c4d"
            }
        }
    }
}
