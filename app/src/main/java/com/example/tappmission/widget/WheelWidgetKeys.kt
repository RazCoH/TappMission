package com.example.tappmission.widget

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
    val STATUS = stringPreferencesKey("status")
    val ERROR_MESSAGE = stringPreferencesKey("error_message")
    val WIDGET_NAME = stringPreferencesKey("widget_name")
    val WIDGET_TYPE = stringPreferencesKey("widget_type")
    val WIDGET_VERSION = stringPreferencesKey("widget_version")
    val WIDGET_HOST = stringPreferencesKey("widget_host")

    const val STATUS_LOADING = "loading"
    const val STATUS_SUCCESS = "success"
    const val STATUS_ERROR = "error"
}
