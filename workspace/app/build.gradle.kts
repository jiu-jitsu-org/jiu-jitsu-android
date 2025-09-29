import java.util.Properties

plugins {
    alias(libs.plugins.jjs.android.application)
    alias(libs.plugins.jjs.android.compose.application)
    alias(libs.plugins.kotlin.serialization)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.kyu.jiu_jitsu"

    defaultConfig {
        applicationId = "com.kyu.jiu_jitsu"
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
            val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField ("boolean", "DEV", "false")
            buildConfigField("String", "KAKAO_KEY",kakaoKey)

            manifestPlaceholders["KAKAO_APP_KEY"] = kakaoKey
//            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

            buildConfigField("boolean", "DEV", "true")
            buildConfigField("String", "KAKAO_KEY",kakaoKey)

            manifestPlaceholders["KAKAO_APP_KEY"] = kakaoKey
        }
    }
}

dependencies {

    implementation(projects.core.ui)
    implementation(projects.feature.login)

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

    implementation(libs.kakao.user)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}