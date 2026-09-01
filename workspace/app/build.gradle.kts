import org.gradle.api.tasks.Copy
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

val ossOutputTimestampFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
val ossOutputDirectory = rootProject.layout.projectDirectory.dir("outputs")

fun ossArtifactFileName(buildType: String, extension: String): String =
    "oss_project_${buildType}_(${LocalDateTime.now().format(ossOutputTimestampFormatter)}).$extension"

fun registerOssArtifactCopyTask(
    copyTaskName: String,
    buildTaskName: String,
    buildType: String,
    artifactDirectory: String,
    extension: String,
) {
    val sourceDirectory = layout.buildDirectory.dir(artifactDirectory)
    val copyTask = tasks.register<Copy>(copyTaskName) {
        from(sourceDirectory) {
            include("*.$extension")
        }
        into(ossOutputDirectory)
        rename { ossArtifactFileName(buildType, extension) }
        outputs.upToDateWhen { false }
        onlyIf {
            sourceDirectory.get().asFile
                .listFiles { file -> file.isFile && file.extension == extension }
                ?.isNotEmpty() == true
        }
    }

    tasks.named(buildTaskName).configure {
        finalizedBy(copyTask)
    }
}

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
//            val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField ("boolean", "DEV", "false")
        }

        getByName("debug") {

            buildConfigField("boolean", "DEV", "true")
        }
    }
}

dependencies {

    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.feature.login)
    implementation(projects.feature.nickname)
    implementation(projects.feature.profile)

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

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

afterEvaluate {
    registerOssArtifactCopyTask(
        copyTaskName = "copyDebugApkToOssOutputs",
        buildTaskName = "assembleDebug",
        buildType = "debug",
        artifactDirectory = "outputs/apk/debug",
        extension = "apk",
    )

    registerOssArtifactCopyTask(
        copyTaskName = "copyReleaseApkToOssOutputs",
        buildTaskName = "assembleRelease",
        buildType = "release",
        artifactDirectory = "outputs/apk/release",
        extension = "apk",
    )

    registerOssArtifactCopyTask(
        copyTaskName = "copyDebugAabToOssOutputs",
        buildTaskName = "bundleDebug",
        buildType = "debug",
        artifactDirectory = "outputs/bundle/debug",
        extension = "aab",
    )

    registerOssArtifactCopyTask(
        copyTaskName = "copyReleaseAabToOssOutputs",
        buildTaskName = "bundleRelease",
        buildType = "release",
        artifactDirectory = "outputs/bundle/release",
        extension = "aab",
    )
}
