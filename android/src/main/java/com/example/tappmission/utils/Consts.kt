package com.example.tappmission.utils

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tappmission.widget.WheelAppWidget
import com.example.tappmission.widget.WheelAppWidgetReceiver

object UrlPaths {
    const val BASE_URL = "https://drive.google.com/uc?export=download&"
    const val CONFIG_PATH = "1TCOGD961TPmtp2EQbvOj6T6wQVkZbjur"
}

object AssetsPaths {
    const val BG_PATH = "1LQBHiIrO92sZ1lFaaqkH_yE5G7A6tK5B"
    const val WHEEL_FRAME_PATH = "10cFF-MGK_rbEh8TnprrmS0uHBOUN7wjN"
    const val WHEEL_SPIN_PATH = "1qx0XNFz6wueMRES02D0QS27fMDfxoBAJ"
    const val WHEEL_PATH = "1gRxQmL7kLnxlTKRk6TKa-YaRKcf61tI9"
}

object Time {
    const val ONE_SECOND = 1000L
    const val ONE_MINUTE = 60 * ONE_SECOND
}

/**
 * Holds all DataStore Preference keys and status constants for the widget.
 *
 * Glance widgets live in a separate process from the app. They can't use
 * ViewModel or in-memory state. DataStore Preferences is a lightweight
 * key-value store that survives process boundaries and persists across reboots.
 * [WheelAppWidgetReceiver] writes to these keys; [WheelAppWidget] reads them.
 */
object WheelWidgetKeys {

    val ROTATION_ANGLE = floatPreferencesKey("rotation_angle")
    val STATUS = stringPreferencesKey("status")
    val ERROR_MESSAGE = stringPreferencesKey("error_message")

    /**
     * The cacheExpiration value (in ms) received from the API response.
     * Stored here so the receiver can reference the last-known expiration
     * on subsequent updates even if the API is temporarily unreachable.
     */
    val CACHE_EXPIRATION = longPreferencesKey("cache_expiration")

    /** Spin animation duration in milliseconds from RotationConfig. */
    val ROTATION_DURATION = longPreferencesKey("rotation_duration")

    /** Minimum number of full wheel rotations per spin from RotationConfig. */
    val ROTATION_MIN_SPINS = intPreferencesKey("rotation_min_spins")

    /** Maximum number of full wheel rotations per spin from RotationConfig. */
    val ROTATION_MAX_SPINS = intPreferencesKey("rotation_max_spins")

    const val STATUS_LOADING = "loading"
    const val STATUS_SUCCESS = "success"
    const val STATUS_ERROR = "error"
}