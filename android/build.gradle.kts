plugins {
    id("com.android.library") version "9.0.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
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
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.12.4")

    // --- Compose UI Framework ---
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // --- Unit Testing ---
    testImplementation("junit:junit:4.13.2")

    // --- UI Testing (Instrumented) ---
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // --- Debug Tools ---
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- Networking ---
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    // --- JSON Serialization ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    // --- Dependency Injection ---
    implementation("io.insert-koin:koin-android:4.1.1")
    implementation("io.insert-koin:koin-core:4.1.1")
    implementation("io.insert-koin:koin-androidx-compose:4.1.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

    // --- Image Loading ---
    implementation("io.coil-kt:coil:2.7.0")

    // --- Home Screen Widget ---
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // --- App Startup (self-initialization without requiring host app changes) ---
    implementation("androidx.startup:startup-runtime:1.2.0")

    // --- React Native Bridge (compileOnly: the host RN app provides this at runtime) ---
    compileOnly("com.facebook.react:react-android:0.76.5")
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