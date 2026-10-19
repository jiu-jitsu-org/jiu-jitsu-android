plugins {
    alias(libs.plugins.jjs.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "kr.bjj_oss.setting"
}

dependencies {
    implementation(libs.kotlinx.serialization)
    implementation(projects.core.data)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(projects.core.model)
}
