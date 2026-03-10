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
import com.example.tappmission.utils.WheelWidgetKeys
import com.example.tappmission.widget.SpinActionCallback
import com.example.tappmission.utils.extensions.getSafeRotatedBitmap

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
            val displayBitmap = if (angle != 0f) it.getSafeRotatedBitmap(angle) else it

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
