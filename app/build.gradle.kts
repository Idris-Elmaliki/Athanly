plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Required for Navigation 3 type-safe routes — routes are @Serializable data objects
    alias(libs.plugins.kotlin.serialization)
    // Hilt dependency injection
    alias(libs.plugins.hilt)
    // KSP — Kotlin Symbol Processing, required by Hilt to generate DI code at compile time
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.athanly"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.athanly"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    // Material Icons — provides Icons.AutoMirrored and the extended icon set
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    testImplementation(libs.junit)
    // Mockk — mocking dependencies in ViewModel/UseCase/Repository unit tests
    testImplementation(libs.mockk)
    // Turbine — asserting StateFlow/Flow emissions in tests
    testImplementation(libs.turbine)
    // kotlinx-coroutines-test — runTest/TestDispatchers for coroutine-based tests
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // hiltViewModel() support in Compose
    implementation(libs.hilt.navigation.compose)

    // collectAsStateWithLifecycle()
    implementation(libs.androidx.lifecycle.runtime.compose)

    // WindowSizeClass and WindowWidthSizeClass
    implementation(libs.androidx.window.core)

    // currentWindowAdaptiveInfo()
    implementation(libs.material3.adaptive)

    // datastore-preferences — persists small preference data (e.g. onboarding completion)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Room — persists cached prayer schedules per city (androidx.room)
    implementation(libs.androidx.room.runtime)
    // Room coroutines/Flow support for suspend DAO functions (androidx.room)
    implementation(libs.androidx.room.ktx)
    // Room annotation processor, run via KSP like the Hilt compiler (androidx.room)
    ksp(libs.androidx.room.compiler)
}