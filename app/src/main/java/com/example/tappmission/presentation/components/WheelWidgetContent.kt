package com.example.tappmission.presentation.components

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.*
import androidx.glance.layout.*
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import com.example.tappmission.widget.WheelWidgetKeys
import com.example.tappmission.widget.SpinActionCallback
import androidx.core.graphics.createBitmap

@Composable
fun WheelWidgetContent(
    wheelBitmap: Bitmap?,
    backgroundBitmap: Bitmap?,
    frameBitmap: Bitmap?,
    buttonBitmap: Bitmap?,
) {

    val prefs = currentState<Preferences>()
    val angle = prefs[WheelWidgetKeys.ROTATION_ANGLE] ?: 0f

    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        backgroundBitmap?.let {
            Image(
                provider = ImageProvider(it),
                contentDescription = "Background",
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }


        wheelBitmap?.let {
            val displayBitmap = if (angle != 0f) getSafeRotatedBitmap(it, angle) else it

            Image(
                provider = ImageProvider(displayBitmap),
                contentDescription = "Wheel",
                modifier = GlanceModifier.size(160.dp),
                contentScale = ContentScale.Fit
            )
        }

        frameBitmap?.let {
            Image(
                provider = ImageProvider(it),
                contentDescription = "Frame",
                modifier = GlanceModifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
        }

        buttonBitmap?.let {
            Image(
                provider = ImageProvider(it),
                contentDescription = "Spin Button",
                modifier = GlanceModifier
                    .size(80.dp)
                    .clickable(actionRunCallback<SpinActionCallback>()),
                contentScale = ContentScale.Fit
            )
        }
    }
}


/**
 * Rotates a bitmap while maintaining a fixed canvas size to prevent "shrinking" in Widgets.
 * * Strategy: By drawing the rotated bitmap onto a fixed-size square canvas that is slightly
 * larger than the source, the resulting Bitmap dimensions never change. This ensures that
 * Jetpack Glance/RemoteViews does not try to rescale the image during animation frames.
 *
 * @param source The original wheel bitmap.
 * @param angle The current rotation angle in degrees (0-360).
 * @return A new, fixed-size Bitmap containing the rotated wheel.
 */
fun getSafeRotatedBitmap(source: Bitmap, angle: Float): Bitmap {

    // Calculate a fixed square size based on the diagonal. add a 10% buffer to ensure corners are never.
    val size = (maxOf(source.width, source.height) * 1.1).toInt()
    val newBitmap = createBitmap(size, size)
    val canvas = android.graphics.Canvas(newBitmap)
    val matrix = android.graphics.Matrix()

    // Apply rotation around the center of the ORIGINAL source bitmap.
    matrix.postRotate(angle, source.width / 2f, source.height / 2f)

    // Center the rotated bitmap
    val translateX = (size - source.width) / 2f
    val translateY = (size - source.height) / 2f
    matrix.postTranslate(translateX, translateY)

    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(source, matrix, paint)

    return newBitmap
}
