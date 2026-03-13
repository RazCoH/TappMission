plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

android {
    namespace = "com.example.tappmission"
    compileSdk = 36

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    defaultConfig {
        minSdk = 26
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
        buildConfig = true
    }
}

dependencies {
    // --- Core Android & Lifecycle ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Compose UI Framework ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- Unit Testing ---
    testImplementation(libs.junit)

    // --- UI Testing (Instrumented) ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- Debug Tools ---
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- Networking ---
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // --- JSON Serialization ---
    implementation(libs.kotlinx.serialization.json)

    // --- Dependency Injection ---
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // --- Image Loading ---
    implementation(libs.coil)

    // --- Home Screen Widget ---
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)

    // --- App Startup (self-initialization without requiring host app changes) ---
    implementation(libs.androidx.startup)

    // --- React Native Bridge (compileOnly: the host RN app provides this at runtime) ---
    compileOnly(libs.react.android)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.RazCoH"
                artifactId = "TappMission"
            }
        }
    }
}