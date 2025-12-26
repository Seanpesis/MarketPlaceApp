plugins {
    id("com.android.application") version "8.13.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}