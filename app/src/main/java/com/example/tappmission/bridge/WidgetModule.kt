package com.example.tappmission.bridge

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import com.example.tappmission.widget.WheelAppWidgetReceiver
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Exposes widget control to the React Native JS layer under the name "WidgetSDK".
 *
 * The module is registered via [WidgetPackage] and accessed in JS with:
 * ```js
 * import { NativeModules } from 'react-native';
 * NativeModules.WidgetSDK.updateWidget();
 * ```
 */
class WidgetModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "WidgetSDK"

    /**
     * Triggers an immediate refresh of all active Wheel Widget instances.
     *
     * Sends [AppWidgetManager.ACTION_APPWIDGET_UPDATE] with the IDs of every
     * widget currently on the home screen. If no widgets are pinned, the
     * broadcast is a no-op.
     *
     * Runs on [Dispatchers.IO] to keep the main thread free.
     * Resolves the [promise] on success or rejects it with an error code so
     * the JS caller can handle failures gracefully.
     *
     * @param promise Resolved with `null` on success; rejected with
     *   `"WIDGET_UPDATE_ERROR"` if an exception is thrown.
     */
    @ReactMethod
    fun updateWidget(promise: Promise) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val context = reactApplicationContext
                val manager = AppWidgetManager.getInstance(context)

                val widgetIds = manager.getAppWidgetIds(
                    ComponentName(context, WheelAppWidgetReceiver::class.java)
                )

                val intent = Intent(context, WheelAppWidgetReceiver::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                }

                context.sendBroadcast(intent)
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("WIDGET_UPDATE_ERROR", e.message ?: "Unknown error", e)
            }
        }
    }
}
