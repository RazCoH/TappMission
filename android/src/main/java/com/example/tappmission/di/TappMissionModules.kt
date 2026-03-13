package com.example.tappmission.di

/**
 * Returns all Koin modules required by the TappMission SDK.
 *
 * Use this if your app already manages its own Koin setup and you want
 * to include the SDK's dependencies manually:
 *
 * ```kotlin
 * startKoin {
 *     androidContext(this@MyApp)
 *     modules(tappMissionModules() + myOwnModules)
 * }
 * ```
 *
 * If you don't call this, the SDK self-initializes via androidx.startup
 * and no host app changes are required.
 */
fun tappMissionModules() = listOf(networkModule, repositoryModule, interactorModule)
