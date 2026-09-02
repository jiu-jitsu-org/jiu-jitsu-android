import java.util.Properties

plugins {
    alias(libs.plugins.jjs.android.feature)
    alias(libs.plugins.secrets)
    alias(libs.plugins.compose.screenshot)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.kyu.jiu_jitsu.login"

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

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

    testOptions {
        screenshotTests {
            imageDifferenceThreshold = 0.0001f
        }
    }
}

dependencies {

    implementation(projects.core.data)
    implementation(projects.core.model)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // SNS Login
    implementation(libs.kakao.user)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.googleid)

    testImplementation(libs.junit)

    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
