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
        applicationId = "com.idris.athanly"
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
    // androidx.core:core-splashscreen — backports the Android 12 SplashScreen API to minSdk 28
    implementation(libs.androidx.core.splashscreen)
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

    // Ktor Client — core HTTP client used to call the Athan prayer-times API (io.ktor)
    implementation(libs.ktor.client.core)
    // Ktor Client — OkHttp engine, the standard HTTP engine choice on Android (io.ktor)
    implementation(libs.ktor.client.okhttp)
    // Ktor Client — negotiates JSON content type for requests/responses (io.ktor)
    implementation(libs.ktor.client.content.negotiation)
    // Ktor Client — logs outgoing requests/responses for debugging network calls (io.ktor)
    implementation(libs.ktor.client.logging)
    // Ktor Client — kotlinx.serialization JSON converter, reuses the existing kotlinx-serialization-json dependency (io.ktor)
    implementation(libs.ktor.serialization.kotlinx.json)

    // FusedLocationProviderClient — fetches the user's current location for the Location onboarding step (com.google.android.gms)
    implementation(libs.play.services.location)

    // WorkManager — schedules the daily worker that refreshes prayer schedules and re-arms alarms (androidx.work)
    implementation(libs.androidx.work.runtime.ktx)
    // Hilt WorkManager integration — lets HiltWorker classes receive @Inject dependencies (androidx.hilt)
    implementation(libs.androidx.hilt.work)
    // Annotation processor for @HiltWorker, run via KSP like the other Hilt/Room processors (androidx.hilt)
    ksp(libs.androidx.hilt.compiler)

    // error_prone_annotations — Dagger declares this compileOnly, so Hilt's generated
    // component code (which references @CanIgnoreReturnValue) needs it added explicitly here (com.google.errorprone)
    implementation(libs.error.prone.annotations)
}