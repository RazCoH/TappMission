package com.example.tappmission.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.tappmission.presentation.components.WheelWidgetContent
import com.example.tappmission.presentation.components.ErrorContent
import com.example.tappmission.presentation.components.LoadingContent
import com.example.tappmission.utils.WheelWidgetKeys

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

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent(context)
            }
        }
    }
}

/**
 * Root composable. Reads status from DataStore and routes to the correct content.
 *
 * Bitmaps are loaded from disk here — inside the composable — rather than
 * once in provideGlance(). This is the key fix: provideGlance() is called
 * once when the Glance session starts, but subsequent state changes only
 * trigger recomposition (not a new provideGlance() call). Loading bitmaps
 * here ensures they are read fresh from disk on every recomposition, so the
 * transition loading → success always finds the downloaded files.
 *
 * Glance composables run on a background thread, so synchronous file I/O
 * via loadCachedBitmap() is safe here.
 */
@Composable
private fun WidgetContent(context: Context) {
    val prefs = currentState<Preferences>()
    val status = prefs[WheelWidgetKeys.STATUS] ?: WheelWidgetKeys.STATUS_LOADING

    when (status) {
        WheelWidgetKeys.STATUS_LOADING -> LoadingContent()
        WheelWidgetKeys.STATUS_SUCCESS -> {
            val wheelBitmap = loadCachedBitmap(context, AssetType.WHEEL)
            val bgBitmap = loadCachedBitmap(context, AssetType.BACKGROUND)
            val frameBitmap = loadCachedBitmap(context, AssetType.FRAME)
            val spinBitmap = loadCachedBitmap(context, AssetType.SPIN)
            // Guard: if bitmaps are missing the interactor hasn't finished yet.
            // Show loading rather than an empty blank widget.
            if (wheelBitmap != null && bgBitmap != null) {
                WheelWidgetContent(wheelBitmap, bgBitmap, frameBitmap, spinBitmap)
            } else {
                LoadingContent()
            }
        }
        else -> ErrorContent()
    }
}
