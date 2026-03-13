package com.example.tappmission.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.tappmission.domain.WheelWidgetInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * System entry point for the widget. Its sole responsibility is to receive
 * system events and forward them to [WheelWidgetInteractor].
 *
 * All business logic lives in the interactor, keeping this class focused and easy to reason about.
 *
 * BroadcastReceivers are not created by Koin, so we can't inject via the
 * constructor. KoinComponent lets us use `by inject()` to pull dependencies
 * out of the Koin graph from anywhere.
 */
class WheelAppWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    /** Glance needs this reference to know which widget class to redraw. */
    override val glanceAppWidget: GlanceAppWidget = WheelAppWidget()

    private val interactor: WheelWidgetInteractor by inject()

    /**
     * Called by the system when the widget is first added to the home screen
     * and periodically (every updatePeriodMillis from wheel_widget_info.xml).
     *
     * Why goAsync()?
     * BroadcastReceivers must finish within ~10 seconds or the system kills
     * the process. goAsync() keeps the process alive long enough for the
     * coroutine to complete. finish() is always called in finally.
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
                interactor.refreshWidgetData()
            } finally {
                pendingResult?.finish()
            }
        }
    }
}
