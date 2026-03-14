apply(plugin = "com.android.library")
apply(plugin = "org.jetbrains.kotlin.android")
apply(plugin = "org.jetbrains.kotlin.plugin.compose")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

configure<com.android.build.gradle.LibraryExtension> {
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
    "implementation"("androidx.core:core-ktx:1.17.0")
    "implementation"("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    "implementation"("androidx.activity:activity-compose:1.12.4")

    "implementation"(platform("androidx.compose:compose-bom:2026.02.01"))
    "implementation"("androidx.compose.ui:ui")
    "implementation"("androidx.compose.ui:ui-graphics")
    "implementation"("androidx.compose.ui:ui-tooling-preview")
    "implementation"("androidx.compose.material3:material3")

    "testImplementation"("junit:junit:4.13.2")
    "androidTestImplementation"("androidx.test.ext:junit:1.3.0")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.7.0")
    "androidTestImplementation"(platform("androidx.compose:compose-bom:2026.02.01"))
    "androidTestImplementation"("androidx.compose.ui:ui-test-junit4")

    "debugImplementation"("androidx.compose.ui:ui-tooling")
    "debugImplementation"("androidx.compose.ui:ui-test-manifest")

    "implementation"("com.squareup.okhttp3:okhttp:5.3.2")
    "implementation"("com.squareup.okhttp3:logging-interceptor:5.3.2")

    // Downgraded from 1.10.0 — must match Kotlin 2.1.x metadata compatibility
    "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    "implementation"("io.insert-koin:koin-android:4.1.1")
    "implementation"("io.insert-koin:koin-core:4.1.1")
    "implementation"("io.insert-koin:koin-androidx-compose:4.1.1")
    "implementation"("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

    "implementation"("io.coil-kt:coil:2.7.0")

    "implementation"("androidx.glance:glance-appwidget:1.1.1")
    "implementation"("androidx.glance:glance-material3:1.1.1")

    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    "implementation"("androidx.startup:startup-runtime:1.2.0")

    "compileOnly"("com.facebook.react:react-android:0.76.5")
}
