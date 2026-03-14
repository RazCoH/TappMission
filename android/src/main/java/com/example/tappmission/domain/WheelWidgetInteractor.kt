package com.example.tappmission.domain

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.tappmission.data.models.DataResult
import com.example.tappmission.data.remote.responses.WheelAssets
import com.example.tappmission.data.repositories.WidgetsRepository
import com.example.tappmission.utils.WheelWidgetKeys
import com.example.tappmission.widget.AssetType
import com.example.tappmission.widget.WheelAppWidget
import com.example.tappmission.widget.getBitmapWithCache
import com.example.tappmission.widget.loadCachedBitmap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Domain interactor that owns all widget data and state management logic.
 *
 * Extracting this logic out of [com.example.tappmission.widget.WheelAppWidgetReceiver] means the receiver
 * becomes a pure "trigger", while all decisions about what state to write
 * and when live here — making the business logic independently testable
 * without a live AppWidgetManager or BroadcastReceiver lifecycle.
 */
class WheelWidgetInteractor(
    private val context: Context,
    private val repository: WidgetsRepository
) {

    private val glanceAppWidget = WheelAppWidget()

    /**
     * Orchestrates a full widget refresh cycle for every currently placed instance:
     *
     * 1. Show loading on all instances immediately for responsive feedback.
     * 2. Fetch the latest config from the repository (network or cache).
     * 3. On success — download/reuse all 4 bitmap layers in parallel, then
     *    write the success state and all persisted config values.
     * 4. On any failure — write error state with a descriptive message.
     *
     * All placed instances are updated together because the user may have
     * pinned the same widget on multiple home screen pages simultaneously.
     */
    suspend fun refreshWidgetData() {
        val glanceIds = GlanceAppWidgetManager(context)
            .getGlanceIds(WheelAppWidget::class.java)

        glanceIds.forEach { id ->
            updateWidgetState(id) {
                this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_LOADING
            }
        }

        when (val result = repository.getWheelWidgetData()) {
            is DataResult.Success -> {
                val widget = result.data.widgets?.firstOrNull()
                val cacheExpiration = widget?.network?.attributes?.cacheExpiration ?: 0L
                val assets = widget?.wheel?.wheelAssets
                val rotationConfig = widget?.wheel?.rotation

                // Download or validate/reuse cached bitmaps for all 4 layers in parallel.
                fetchAllBitmaps(cacheExpiration, assets)

                // Only report success if the bitmaps are actually on disk.
                // fetchAllBitmaps swallows download/write failures silently, so we
                // verify here rather than unconditionally setting STATUS_SUCCESS and
                // leaving the widget with a blank WheelWidgetContent(null bitmaps).
                val bitmapsReady = AssetType.entries.all {
                    loadCachedBitmap(context, it) != null
                }
                if (!bitmapsReady) {
                    applyErrorToAll(glanceIds, "Widget images could not be loaded")
                    return
                }

                glanceIds.forEach { id ->
                    updateWidgetState(id) {
                        this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_SUCCESS
                        this[WheelWidgetKeys.CACHE_EXPIRATION] = cacheExpiration
                        // Persist rotation config so SpinActionCallback can read it
                        // without a network call when the user taps spin.
                        rotationConfig?.duration?.let {
                            this[WheelWidgetKeys.ROTATION_DURATION] = it.toLong()
                        }
                        rotationConfig?.minimumSpins?.let {
                            this[WheelWidgetKeys.ROTATION_MIN_SPINS] = it
                        }
                        rotationConfig?.maximumSpins?.let {
                            this[WheelWidgetKeys.ROTATION_MAX_SPINS] = it
                        }
                    }
                }
            }
            is DataResult.Error -> applyErrorToAll(glanceIds, result.msg)
            is DataResult.Exception -> applyErrorToAll(
                glanceIds, result.e.message ?: "Unknown error"
            )
        }
    }

    /**
     * Fetches all 4 wheel-layer bitmaps in parallel.
     * Each call checks the disk cache first; only downloads if expired or missing.
     * coroutineScope ensures all 4 complete before execution continues.
     *
     * If [assets] is null the server sent no image data, so we skip the download
     * entirely and let the widget show whatever is already on disk.
     * If a single layer's URL is null, only that layer is skipped —
     * the others still download normally.
     */
    private suspend fun fetchAllBitmaps(
        cacheExpiration: Long,
        assets: WheelAssets?
    ) {
        if (assets == null) return
        coroutineScope {
            AssetType.entries.forEach { assetType ->
                launch {
                    val url = assetType.buildUrl(assets)
                    getBitmapWithCache(context, url, assetType, cacheExpiration)
                }
            }
        }
    }

    /**
     * Writes error state to all instances and triggers a redraw on each.
     * Covers both HTTP errors (DataResult.Error) and exceptions
     * (no internet, timeout, etc.).
     */
    private suspend fun applyErrorToAll(glanceIds: List<GlanceId>, message: String) {
        glanceIds.forEach { id ->
            updateWidgetState(id) {
                this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_ERROR
                this[WheelWidgetKeys.ERROR_MESSAGE] = message
            }
        }
    }

    /**
     * Atomically writes state and triggers a UI redraw for a single widget instance.
     *
     * Pairing both calls here ensures every state mutation is immediately
     * reflected on screen — callers never accidentally forget the redraw.
     */
    private suspend fun updateWidgetState(
        glanceId: GlanceId,
        block: MutablePreferences.() -> Unit
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply(block)
        }
        glanceAppWidget.update(context, glanceId)
    }
}
