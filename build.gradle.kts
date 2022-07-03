import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.compose") version "1.1.1"
}

group = "com.example"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation(compose.materialIconsExtended)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.desktop.components.splitPane)
                runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.+")
            }
        }
        val jvmTest by getting
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}