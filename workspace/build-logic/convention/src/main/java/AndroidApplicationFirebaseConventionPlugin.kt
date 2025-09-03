import com.jiu_jitsu.buildlogic.libs
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.gms.google-services")
            apply(plugin = "com.google.firebase.firebase-perf")
            apply(plugin = "com.google.firebase.crashlytics")

            dependencies {
                val bom = libs.findLibrary("firebase.bom").get()
                "implementation"(platform(bom))
                "implementation"(libs.findLibrary("firebase.analytics").get())
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
                "implementation"(libs.findLibrary("firebase.performance").get())
            }

//            extensions.configure<ApplicationExtension> {
//                buildTypes.configureEach {
//
//                }
//            }
        }
    }
}