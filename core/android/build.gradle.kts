import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("apexowner.android.base")
}

extensions.configure<LibraryExtension> {
    namespace = "com.xxmrk888ytxx.android"
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.activity.compose)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.coroutines.android)
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlin.serialization.core)
    api(libs.timber)
    api(libs.hilt.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}