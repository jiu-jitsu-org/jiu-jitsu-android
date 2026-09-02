import java.util.Properties

plugins {
    alias(libs.plugins.jjs.android.feature)
    alias(libs.plugins.secrets)
    alias(libs.plugins.compose.screenshot)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

fun String.asBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

android {
    namespace = "com.kyu.jiu_jitsu.profile"

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField(
            "String",
            "IMAGE_PUBLIC_KEY",
            properties.getProperty("IMAGE_PUBLIC_KEY", "").asBuildConfigString(),
        )
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.coil.compose)

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
