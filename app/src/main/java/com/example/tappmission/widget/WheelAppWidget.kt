package com.example.tappmission.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

/**
 * The widget definition. Glance uses this class to know:
 * 1. What state storage to use (stateDefinition).
 * 2. What UI to draw (provideGlance).
 */
class WheelAppWidget : GlanceAppWidget() {

    /**
     * Tells Glance to use DataStore Preferences as the backing store for
     * this widget's state. Every time the state changes, Glance
     * automatically re-runs provideGlance and redraws the widget.
     */
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    /**
     * Entry point called by Glance whenever the widget needs to be drawn
     * (first add, system update, or after a state change).
     * provideContent suspends until the UI is ready, so it's safe to do
     * lightweight setup here before handing off to the composables.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }
}

@Composable
private fun WidgetContent() {
    val prefs = currentState<Preferences>()
    val status = prefs[WheelWidgetKeys.STATUS] ?: WheelWidgetKeys.STATUS_LOADING

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        when (status) {
            WheelWidgetKeys.STATUS_LOADING -> LoadingContent()
            WheelWidgetKeys.STATUS_SUCCESS -> SuccessContent(prefs)
            else -> ErrorContent(prefs)
        }
    }
}

@Composable
private fun LoadingContent() {
    Text(
        text = "Loading...",
        style = TextStyle(color = GlanceTheme.colors.onSurface)
    )
}

/**
 * Shown when the network call succeeded.
 * Reads each widget field directly from Preferences — no extra data class
 * is needed because Preferences is already the shared contract between
 * the receiver and the widget.
 */
@Composable
private fun SuccessContent(prefs: Preferences) {
    val name = prefs[WheelWidgetKeys.WIDGET_NAME].orEmpty()
    val type = prefs[WheelWidgetKeys.WIDGET_TYPE].orEmpty()
    val version = prefs[WheelWidgetKeys.WIDGET_VERSION].orEmpty()
    val host = prefs[WheelWidgetKeys.WIDGET_HOST].orEmpty()

    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = name.ifEmpty { "Wheel Widget" },
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        if (type.isNotEmpty()) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Type: $type",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
            )
        }
        if (host.isNotEmpty()) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Host: $host",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
            )
        }
        if (version.isNotEmpty()) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "v$version",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        RefreshButton()
    }
}

/**
 * Shown when the network call failed or threw an exception.
 * The error message is stored in Preferences by the receiver so it
 * can be displayed here without any extra state management.
 */
@Composable
private fun ErrorContent(prefs: Preferences) {
    val errorMsg = prefs[WheelWidgetKeys.ERROR_MESSAGE] ?: "Failed to load data"

    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = "Error",
            style = TextStyle(
                color = GlanceTheme.colors.error,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = errorMsg,
            style = TextStyle(color = GlanceTheme.colors.onSurface)
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        RefreshButton()
    }
}

/**
 * A button that triggers a manual data refresh.
 *
 * Widget buttons cannot call functions directly — the widget UI runs in
 * the launcher's process, not the app's. Instead, actionSendBroadcast
 * fires an Intent to WheelAppWidgetReceiver, which then runs the fetch
 * in the correct process with access to Koin and the repository.
 */
@Composable
private fun RefreshButton() {
    Button(
        text = "Update",
        onClick = actionSendBroadcast(
            Intent(WheelAppWidgetReceiver.ACTION_REFRESH)
                .setPackage("com.example.tappmission")
        )
    )
}
