import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.apexowner.android.feature)
}

extensions.configure<LibraryExtension> {
    namespace = "com.xxmrk888ytxx.common.preferencesstorage"
}

dependencies {
    api(libs.datastore.preferences)
}