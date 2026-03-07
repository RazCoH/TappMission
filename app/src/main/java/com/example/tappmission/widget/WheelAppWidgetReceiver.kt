package com.example.tappmission.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.tappmission.data.models.NetworkResult
import com.example.tappmission.data.repositories.WidgetsRepository
import com.example.tappmission.data.responses.WidgetResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
                pendingResult.finish()
            }
        }
    }

    /**
     * Called for every broadcast this receiver can receive.
     * We filter for ACTION_REFRESH, which is sent when the user taps
     * the "Update" button inside the widget.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fetchAndUpdateAll(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    /**
     * Core logic: updates every instance of this widget on the home screen.
     *
     * The flow is:
     * 1. Find all placed widget instances via GlanceAppWidgetManager.
     * 2. Set STATUS = loading and redraw immediately (user sees a spinner).
     * 3. Make the network call on Dispatchers.IO.
     * 4. Write the result (success or error) into DataStore Preferences.
     * 5. Call update() so Glance re-reads the state and redraws the widget.
     *
     * We loop over all instances because the user could place the same
     * widget multiple times on different home screen pages.
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

        val result = repository.getWheelWidgetData()

        glanceIds.forEach { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                prefs.toMutablePreferences().apply {
                    when (result) {
                        is NetworkResult.Success -> applySuccess(result.data)
                        is NetworkResult.Error -> applyError(result.msg)
                        is NetworkResult.Exception -> applyError(
                            result.e.message ?: "Unknown error"
                        )
                    }
                }
            }
            glanceAppWidget.update(context, id)
        }
    }

    /**
     * Writes success data into the MutablePreferences snapshot.
     * Extension function on MutablePreferences to keep fetchAndUpdateAll clean.
     */
    private fun MutablePreferences.applySuccess(data: WidgetResponse) {
        val widget = data.widgets?.firstOrNull()
        this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_SUCCESS
        this[WheelWidgetKeys.WIDGET_NAME] = widget?.name.orEmpty()
        this[WheelWidgetKeys.WIDGET_TYPE] = widget?.type.orEmpty()
        this[WheelWidgetKeys.WIDGET_VERSION] = data.meta?.version?.toString().orEmpty()
        this[WheelWidgetKeys.WIDGET_HOST] = widget?.network?.assets?.host.orEmpty()
    }

    /**
     * Writes error state into the MutablePreferences snapshot.
     * Covers both HTTP errors (NetworkResult.Error) and exceptions (no internet, timeout).
     */
    private fun MutablePreferences.applyError(message: String) {
        this[WheelWidgetKeys.STATUS] = WheelWidgetKeys.STATUS_ERROR
        this[WheelWidgetKeys.ERROR_MESSAGE] = message
    }

    companion object {
        /** Custom broadcast action fired by the "Update" button in the widget UI. */
        const val ACTION_REFRESH = "com.example.tappmission.widget.ACTION_REFRESH"
    }
}
