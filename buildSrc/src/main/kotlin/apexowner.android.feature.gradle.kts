plugins {
    id("apexowner.android.base")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

dependencies {
    implementation(project(":core:android"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
}
