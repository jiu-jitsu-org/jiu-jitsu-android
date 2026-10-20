import java.util.Properties

plugins {
    alias(libs.plugins.jjs.android.application)
    alias(libs.plugins.jjs.android.compose.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
    alias(libs.plugins.gms)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

// Public frontend origins; local/CI overrides never derive from backend API URLs.
fun webOriginField(key: String, fallback: String): String {
    val value = providers.gradleProperty(key).orNull ?: properties.getProperty(key) ?: fallback
    val origin = value.trim().removeSurrounding("\"").trimEnd('/')
    require(origin.none { it == '\n' || it == '\r' || it == '\\' || it == '"' })
    return "\"$origin\""
}

android {
    namespace = "kr.bjj_oss"

    defaultConfig {
        applicationId = "kr.bjj_oss"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
//            val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField ("boolean", "DEV", "false")
            buildConfigField("String", "OSS_BASE_URL", properties.getProperty("BASE_URL"))
            buildConfigField("String", "OSS_WEB_ORIGIN", webOriginField("WEB_ORIGIN", ""))
            buildConfigField("String", "COMMUNITY_NOTIFICATION_PATH", properties.getProperty("COMMUNITY_NOTIFICATION_PATH"))
            buildConfigField("String", "COMMUNITY_ME_PATH", properties.getProperty("COMMUNITY_ME_PATH"))
        }

        getByName("debug") {

            buildConfigField("boolean", "DEV", "true")
            buildConfigField("String", "OSS_BASE_URL", properties.getProperty("DEV_BASE_URL"))
            buildConfigField("String", "OSS_WEB_ORIGIN", webOriginField("DEV_WEB_ORIGIN", ""))
            buildConfigField("String", "COMMUNITY_NOTIFICATION_PATH", properties.getProperty("COMMUNITY_NOTIFICATION_PATH"))
            buildConfigField("String", "COMMUNITY_ME_PATH", properties.getProperty("COMMUNITY_ME_PATH"))
        }
    }
}

dependencies {

    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.feature.login)
    implementation(projects.feature.nickname)
    implementation(projects.feature.profile)
    implementation(projects.feature.setting)
    implementation(projects.feature.web)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.kakao.user)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(projects.core.webview)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
