plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    // App models are deliberately compiled as plain JVM types so Android, network,
    // persistence, and UI concerns cannot accidentally enter the shared contract.
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.junit)
}
