package com.example.tappmission.data.local

import android.content.Context
import com.example.tappmission.data.remote.responses.WidgetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import androidx.core.content.edit

/**
 * Manages two kinds of persistent local storage for the widget config:
 *
 * 1. The full [WidgetResponse] — serialized as JSON in internal app storage.
 *    Internal storage is private to the app, never backed up by default,
 *    and survives process death.
 *
 * 2. The lastFetchTimestamp stored as a Long in SharedPreferences.
 *    Compared against cacheExpiration to decide whether a fresh network call is needed.
 */
class LocalDataSource(
    private val context: Context,
    private val json: Json
) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val cacheFile: File
        get() = File(context.filesDir, CACHE_FILE_NAME)

    /**
     * Returns the last successfully stored [WidgetResponse], or null if no
     * cache file exists or the file cannot be deserialized.
     */
    suspend fun readCachedResponse(): WidgetResponse? = withContext(Dispatchers.IO) {
        try {
            if (!cacheFile.exists()) return@withContext null
            json.decodeFromString(WidgetResponse.serializer(), cacheFile.readText())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns the Unix timestamp (ms) of the last successful server fetch,
     * or 0 if no fetch has ever been recorded.
     */
    fun getLastFetchTimestamp(): Long = prefs.getLong(KEY_LAST_FETCH, 0L)

    /**
     * Serialises [response] to JSON and writes it to internal storage.
     * Failures are logged and swallowed so a write error never crashes the widget.
     */
    suspend fun writeCachedResponse(response: WidgetResponse) = withContext(Dispatchers.IO) {
        try {
            cacheFile.writeText(json.encodeToString(WidgetResponse.serializer(), response))
        } catch (e: Exception) {
        }
    }

    /**
     * Persists [timestamp] as the time of the most recent successful fetch.
     * Uses `apply()` for a non-blocking background commit.
     */
    fun saveLastFetchTimestamp(timestamp: Long) {
        prefs.edit { putLong(KEY_LAST_FETCH, timestamp) }
    }

    companion object {
        private const val PREFS_NAME = "widget_cache_prefs"
        private const val KEY_LAST_FETCH = "last_fetch_timestamp"
        private const val CACHE_FILE_NAME = "widget_response_cache.json"
    }
}
