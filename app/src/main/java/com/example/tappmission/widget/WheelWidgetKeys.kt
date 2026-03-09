package com.example.tappmission.widget

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

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
