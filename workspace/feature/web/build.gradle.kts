plugins { alias(libs.plugins.jjs.android.feature) }
android { namespace = "kr.bjj_oss.web" }
dependencies {
    implementation(projects.core.webview)
    implementation(projects.core.data)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    testImplementation(libs.junit)
}
