import java.util.Properties

plugins {
    alias(libs.plugins.jjs.android.library)
    alias(libs.plugins.jjs.hilt)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.kyu.jiu_jitsu.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // 개발여부설정 : false
            buildConfigField ("boolean", "DEV", "false")
            buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
//            buildConfigField("String", "NAVER_MAP_BASE_URL", properties.getProperty("NAVER_MAP_URL"))
//            buildConfigField("String", "GOOGLE_CLOUD_CLIENT_ID", properties.getProperty("GOOGLE_CLOUD_CLIENT_ID"))
        }
        getByName("debug") {
            // 개발여부설정 : true
            buildConfigField("boolean", "DEV", "true")
            buildConfigField("String", "BASE_URL", properties.getProperty("DEV_BASE_URL"))
//            buildConfigField("String", "NAVER_MAP_BASE_URL", properties.getProperty("NAVER_MAP_URL"))
//            buildConfigField("String", "GOOGLE_CLOUD_CLIENT_ID", properties.getProperty("GOOGLE_CLOUD_CLIENT_ID"))
        }

    }
}

dependencies {

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp.profiler)
    implementation(libs.androidx.datastore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}