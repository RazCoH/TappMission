package com.example.tappmission.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.tappmission.data.models.DataResult
import com.example.tappmission.data.repositories.WidgetsRepository
import com.example.tappmission.data.remote.responses.WheelAssets
import com.example.tappmission.utils.WheelWidgetKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.apply

/**
 * The "controller" of the widget. It is a BroadcastReceiver that the
 * Android system calls when something needs to happen (widget added,
 * periodic refresh, or user tapped "Update").
 *
 * Why KoinComponent?
 * BroadcastReceivers are not created by Koin, so we can't inject via
 * the constructor. KoinComponent lets us use `by inject()` to pull
 * dependencies out of the Koin graph from anywhere.
 */
class WheelAppWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    /** Glance needs this reference to know which widget class to redraw. */
    override val glanceAppWidget: GlanceAppWidget = WheelAppWidget()

    private val repository: WidgetsRepository by inject()

    /**
     * Called by the system when the widget is first added to the home
     * screen and also periodically (every updatePeriodMillis from
     * wheel_widget_info.xml). We use it to trigger an initial data load.
     *
     * Why goAsync()?
     * BroadcastReceivers must finish within ~10 seconds or the system
     * kills the process. goAsync() tells Android "I'm not done yet",
     * giving us time to run the network call. We call finish() when done.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fetchAndUpdateAll(context)
            } finally {
                pendingResult?.finish()
            }
        }
    }

    /**
     * Core logic: updates every placed instance of this widget.
     *
     * Flow:
     * 1. Set STATUS = loading on all instances and redraw (immediate feedback).
     * 2. Fetch the config from the API.
     * 3. On success — download or reuse cached bitmaps for all 4 wheel layers,
     *    then set STATUS = success + persist the cache expiration value.
     * 4. On error — set STATUS = error with a message.
     * 5. Redraw all instances with the final state.
     *
     * We loop over all glanceIds because the user can place the same widget
     * on multiple home screen pages at once.
     */
    private suspend fun fetchAndUpdateAll(context: Context) {
        val glanceIds = GlanceAppWidgetManager(context)
            .getGlanceIds(WheelAppWidget::class.java)

        glanceIds.forEach { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_LOADING
                }
            }
            glanceAppWidget.update(context, id)
        }

        when (val result = repository.getWheelWidgetData()) {
            is DataResult.Success -> {
                val widget = result.data.widgets?.firstOrNull()
                val cacheExpiration = widget?.network?.attributes?.cacheExpiration ?: 0L
                val assets = widget?.wheel?.wheelAssets
                val rotationConfig = widget?.wheel?.rotation

                // Download or validate/reuse cached bitmaps for all 4 layers in parallel.
                // getBitmapWithCache saves each file to cacheDir as a side-effect;
                // provideGlance will read them from disk when it redraws the widget.
                fetchAllBitmaps(context, cacheExpiration, assets)

                glanceIds.forEach { id ->
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                        prefs.toMutablePreferences().apply {
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
                    glanceAppWidget.update(context, id)
                }
            }
            is DataResult.Error -> applyErrorToAll(context, glanceIds, result.msg)
            is DataResult.Exception -> applyErrorToAll(
                context, glanceIds, result.e.message ?: "Unknown error"
            )
        }
    }

    /**
     * Fetches all 4 wheel-layer bitmaps in parallel.
     * Each call checks the disk cache first; only downloads if expired or missing.
     * coroutineScope ensures we wait for all 4 before continuing.
     *
     * If [assets] is null the server sent no image data, so we skip the
     * download entirely and let the widget show whatever is already on disk.
     * If a single layer's URL is null (field missing from response), only
     * that layer is skipped — the others still download normally.
     */
    private suspend fun fetchAllBitmaps(
        context: Context,
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
     * Writes error state to all widget instances and triggers a redraw.
     * Extracted to avoid repeating the forEach block for each error branch.
     */
    private suspend fun applyErrorToAll(
        context: Context,
        glanceIds: List<GlanceId>,
        message: String
    ) {
        glanceIds.forEach { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                prefs.toMutablePreferences().apply { applyError(message) }
            }
            glanceAppWidget.update(context, id)
        }
    }

    /**
     * Writes error state into the MutablePreferences snapshot.
     * Covers both HTTP errors (NetworkResult.Error) and exceptions
     * (no internet, timeout, etc.).
     */
    private fun MutablePreferences.applyError(message: String) {
        this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_ERROR
        this[WheelWidgetKeys.ERROR_MESSAGE] = message
    }
}
