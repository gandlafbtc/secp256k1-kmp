plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka")
    `maven-publish`
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":jni"))
}

android {
    defaultConfig {
        compileSdkVersion(30)
        minSdkVersion(21)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {}
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    externalNativeBuild {
        cmake {
            path("src/main/CMakeLists.txt")
        }
    }
    ndkVersion = "21.4.7075529"

    afterEvaluate {
        tasks.withType<com.android.build.gradle.tasks.factory.AndroidUnitTest>().all {
            enabled = false
        }
    }
}

afterEvaluate {
    configure(listOf("Debug", "Release").map { tasks["externalNativeBuild$it"] }) {
        dependsOn(":native:buildSecp256k1Android")
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("android") {
                artifactId = "secp256k1-kmp-jni-android"
                from(components["release"])
                val sourcesJar = task<Jar>("sourcesJar") {
                    archiveClassifier.set("sources")
                    from(android.sourceSets["main"].java.srcDirs)
                }
                artifact(sourcesJar)
            }
        }
    }
}
