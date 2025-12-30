import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "app.habit_lens.ui_kotlin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.habit_lens.ui_kotlin"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            // ────────────────────────────────────────────────────────────────
            // Option 1 – Best for most teams (recommended in 2025)
            // Key is stored right next to build.gradle.kts (same folder)
            // → Very clean, project is portable, .gitignore friendly
            // ────────────────────────────────────────────────────────────────
//            storeFile = file("my-release-key.jks")
            // Option 2 – Also very good – key in project root (one level up)
//             storeFile = file("$rootDir/my-release-key.jks")
             storeFile = file("/home/shd/scratches/ui-kotlin/my-release-key.jks")
            // storeFile = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "my-release-key.jks")
            storePassword = "123123"           // ← better to use properties/env vars!
            keyAlias = "myalias"
            keyPassword = "123123"             // ← same here
            enableV1Signing = true             // still needed for old Android <7.0
            enableV2Signing = true
            enableV3Signing = true             // now very widely supported
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = false           // change to true later + add real proguard-rules.pro
            isShrinkResources = false         // ← usually pair with minifyEnabled

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

}

compose.desktop {
    application {
        mainClass = "app.habit_lens.ui_kotlin.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "app.habit_lens.ui_kotlin"
            packageVersion = "1.0.0"
        }
    }
}
