import java.util.Properties

plugins {
    alias(libs.plugins.jjs.android.feaure)
    alias(libs.plugins.jjs.android.compose.library)
    alias(libs.plugins.secrets)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())


android {
    namespace = "com.kyu.jiu_jitsu.nickname"

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
        }
        getByName("debug") {
            // 개발여부설정 : true
            buildConfigField("boolean", "DEV", "true")
        }

    }
}

dependencies {

    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}