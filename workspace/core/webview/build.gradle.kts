plugins {
    alias(libs.plugins.jjs.android.compose.library)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.kyu.jiu_jitsu.webview"
    defaultConfig { consumerProguardFiles("consumer-rules.pro") }
}
dependencies {
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
}
