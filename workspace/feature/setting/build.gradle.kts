plugins {
    alias(libs.plugins.jjs.android.feature)
}

android {
    namespace = "com.kyu.jiu_jitsu.setting"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(projects.core.model)
}
