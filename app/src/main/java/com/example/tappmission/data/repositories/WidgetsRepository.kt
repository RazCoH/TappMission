package com.example.tappmission.data.repositories

import com.example.tappmission.data.local.LocalDataSource
import com.example.tappmission.data.models.DataResult
import com.example.tappmission.data.remote.RemoteDataSource
import com.example.tappmission.data.remote.responses.WidgetResponse
import com.example.tappmission.utils.Time

/**
 * Single source of truth for widget configuration data.
 *
 * Implements a Cache-Aside strategy:
 *   1. Check the local cache. If it is fresh, return it immediately.
 *   2. If the cache is missing or expired, fetch from the remote API.
 *   3. On a successful fetch, persist the response and update the timestamp.
 *   4. If the fetch fails but a stale cache exists, return the stale data as
 *      a fallback so the widget remains functional without a network connection.
 *
 * Cache freshness is determined by comparing the elapsed time since the last
 * successful fetch against the cacheExpiration value that the server itself provides.
 */
class WidgetsRepository(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) {

    suspend fun getWheelWidgetData(): DataResult<WidgetResponse> {
        val cachedResponse = local.readCachedResponse()
        val cacheExpiration =
            cachedResponse
            ?.widgets?.firstOrNull()
            ?.network?.attributes?.cacheExpiration?.let { it * Time.ONE_SECOND }
            ?: 0L

        val isCacheValid = cachedResponse != null
                && cacheExpiration > 0L
                && (System.currentTimeMillis() - local.getLastFetchTimestamp()) <= cacheExpiration

        if (isCacheValid) {
            return DataResult.Success(cachedResponse)
        }

        return when (val result = remote.fetchWheelWidgetData()) {
            is DataResult.Success -> {
                local.writeCachedResponse(result.data)
                local.saveLastFetchTimestamp(System.currentTimeMillis())
                result
            }
            else -> {
                // Network failed. Serve stale data if available so the widget
                // does not break completely when the device is offline.
                if (cachedResponse != null) {
                    DataResult.Success(cachedResponse)
                } else {
                    result
                }
            }
        }
    }
}
