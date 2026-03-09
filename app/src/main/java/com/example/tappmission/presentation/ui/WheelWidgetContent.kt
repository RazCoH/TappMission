package com.example.tappmission.presentation.ui

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.layout.*
import androidx.glance.ImageProvider

@Composable
fun WheelWidgetContent(
    wheelBitmap: Bitmap?,
    backgroundBitmap: Bitmap?,
    frameBitmap: Bitmap?,
    buttonBitmap: Bitmap?
) {
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
            Image(
                provider = ImageProvider(it),
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
                    .size(80.dp),
                    //todo .clickable(actionRunCallback<SpinActionCallback>()),
                contentScale = ContentScale.Fit
            )
        }
    }
}