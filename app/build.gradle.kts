import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.oss.licenses.plugin)
    alias(libs.plugins.hilt.plugin)
}

val catalogJavaVersion = libs.versions.jvm.target.get()
val catalogCompileSdk = libs.versions.compile.sdk.get().toInt()
val catalogMinSdk = libs.versions.min.sdk.get().toInt()

extensions.configure<ApplicationExtension> {
    namespace = "com.xxmrk888ytxx.apexowner"
    compileSdk {
        version = release(catalogCompileSdk) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.xxmrk888ytxx.apexowner"
        minSdk = catalogMinSdk
        targetSdk = catalogCompileSdk
        versionCode = 1
        versionName = "WIP"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$catalogJavaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$catalogJavaVersion")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(projects.core.compose)
    // Common
    implementation(projects.common.preferencesStorage)
    // Feature
    implementation(projects.feature.onboarding)

    // Dependencies
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.androidx.splashScreen)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}