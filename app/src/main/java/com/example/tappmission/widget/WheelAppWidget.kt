package com.example.tappmission.widget

import android.content.Context
import android.graphics.Bitmap
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
     * Entry point called by Glance whenever the widget needs to be drawn.
     *
     * Bitmaps are loaded from the disk cache here — before provideContent —
     * so they are available as a captured closure inside the composable tree.
     * The receiver is responsible for downloading and refreshing those files;
     * provideGlance only reads what is already on disk (fast, no network).
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val wheelBitmap = withContext(Dispatchers.IO) { loadCachedBitmap(context, AssetType.WHEEL) }
        val bgBitmap = withContext(Dispatchers.IO) { loadCachedBitmap(context, AssetType.BACKGROUND) }
        val frameBitmap = withContext(Dispatchers.IO) { loadCachedBitmap(context, AssetType.FRAME) }
        val spinBitmap = withContext(Dispatchers.IO) { loadCachedBitmap(context, AssetType.SPIN) }

        provideContent {
            GlanceTheme {
                WidgetContent(wheelBitmap, bgBitmap, frameBitmap, spinBitmap)
            }
        }
    }
}

/**
 * Root composable. Reads the current status from DataStore Preferences
 * and routes to the correct sub-composable.
 *
 * currentState<Preferences>() is Glance's equivalent of collectAsState() —
 * it automatically subscribes to the DataStore and recomposes when any key
 * changes. The bitmaps come from the outer provideGlance scope (disk reads
 * that already happened before this composable was invoked).
 */
@Composable
private fun WidgetContent(
    wheelBitmap: Bitmap?,
    backgroundBitmap: Bitmap?,
    frameBitmap: Bitmap?,
    spinBitmap: Bitmap?
) {
    val prefs = currentState<Preferences>()
    val status = prefs[WheelWidgetKeys.STATUS] ?: WheelWidgetKeys.STATUS_LOADING

    when (status) {
        WheelWidgetKeys.STATUS_LOADING -> LoadingContent()
        WheelWidgetKeys.STATUS_SUCCESS -> WheelWidgetContent(
            wheelBitmap = wheelBitmap,
            backgroundBitmap = backgroundBitmap,
            frameBitmap = frameBitmap,
            buttonBitmap = spinBitmap
        )
        else -> ErrorContent()
    }
}
