package com.example.tappmission.widget

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Holds all DataStore Preference keys and status constants for the widget.
 *
 * Why DataStore Preferences?
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

    const val STATUS_LOADING = "loading"
    const val STATUS_SUCCESS = "success"
    const val STATUS_ERROR = "error"
}
