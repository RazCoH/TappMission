package com.example.tappmission.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.random.Random

class SpinActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Retrieve current state using the explicit Preferences definition
        val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)

        // Read rotation config persisted by WheelAppWidgetReceiver; fall back to defaults
        // if the widget was never successfully refreshed.
        val duration = prefs[WheelWidgetKeys.ROTATION_DURATION] ?: 2000
        val minSpins = prefs[WheelWidgetKeys.ROTATION_MIN_SPINS] ?: 3
        val maxSpins = prefs[WheelWidgetKeys.ROTATION_MAX_SPINS] ?: 5

        val startTime = System.currentTimeMillis()

        // Start from the last known angle to ensure a seamless transition between spins
        val startAngle = prefs[WheelWidgetKeys.ROTATION_ANGLE] ?: 0f


        val numOfSpins = Random.nextInt(minSpins, maxSpins + 1)
        val fullTurns = numOfSpins * 360f
        val randomOffset = Random.nextFloat() * 360f
        val totalDegreesToRotate = fullTurns + randomOffset

        try {
            while (System.currentTimeMillis() - startTime < duration) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / duration).coerceAtMost(1f)

                // Cubic-Out Easing: Animation starts fast and decelerates smoothly at the end
                val easedProgress = 1f - (1f - progress).pow(2f)
                val currentAngle = startAngle + (easedProgress * totalDegreesToRotate)

                // Update the persistent state with the new calculated angle
                updateAppWidgetState(context, glanceId) { state ->
                    state[WheelWidgetKeys.ROTATION_ANGLE] = currentAngle
                }

                // Force a widget UI refresh
                WheelAppWidget().update(context, glanceId)

                // Maintain ~25 FPS to balance smoothness and system performance
                delay(40)
            }

            // Finalize: Calculate the end angle and normalize it to 0-359° range
            val finalAngle = (startAngle + totalDegreesToRotate) % 360f
            updateAppWidgetState(context, glanceId) { state ->
                state[WheelWidgetKeys.ROTATION_ANGLE] = finalAngle
            }

            // Final refresh to ensure the wheel stops exactly at the calculated result
            WheelAppWidget().update(context, glanceId)

        } catch (e: Exception) {
            android.util.Log.e("SpinAction", "Animation loop failed", e)
        }
    }
}

