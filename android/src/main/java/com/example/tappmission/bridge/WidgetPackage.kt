package com.example.tappmission.bridge

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

/**
 * Registers the SDK's native modules with the React Native runtime.
 *
 * The host React Native app must add an instance of this class to its
 * package list inside `getPackages()` in `MainApplication.kt`:
 * ```kotlin
 * override fun getPackages() = PackageList(this).packages.apply {
 *     add(WidgetPackage())
 * }
 * ```
 */
class WidgetPackage : ReactPackage {

    /** Returns the native modules this package contributes to the RN bridge. */
    override fun createNativeModules(
        reactContext: ReactApplicationContext
    ): List<NativeModule> = listOf(WidgetModule(reactContext))

    /** No custom views are exported by this SDK. */
    override fun createViewManagers(
        reactContext: ReactApplicationContext
    ): List<ViewManager<*, *>> = emptyList()
}
