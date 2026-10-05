plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Use the full plugin ID here
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
}