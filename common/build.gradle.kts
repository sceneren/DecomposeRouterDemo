import gobley.gradle.cargo.dsl.android
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gobley.cargo)
    alias(libs.plugins.gobley.uniffi)
    kotlin("plugin.atomicfu") version libs.versions.kotlin
}

android {
    namespace = "com.github.sceneren.common"
    compileSdk = 35

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        // You can also set it as a string, though the JvmTarget enum is preferred for type safety:
        // jvmTarget.set("11")
    }
}

cargo {
    builds.android {
        variants {
            buildTaskProvider.configure {
                additionalEnvironment.put(
                    "RUSTFLAGS",
                    "-C link-args=-Wl,-z,max-page-size=16384",
                )
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.bundles.decompose.router)
    implementation(libs.bundles.orbit.mvi)

    implementation(libs.kotlinx.collections.immutable)
    api(libs.xlog)

    api(libs.webview)
    //api("wiki.scene.socket:SceneNettySocket:1.1.3")
}