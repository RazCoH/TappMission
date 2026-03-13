package com.example.tappmission.utils.extensions

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap

/**
 * Rotates a bitmap while maintaining a fixed canvas size to prevent "shrinking" in Widgets.
 * * Strategy: By drawing the rotated bitmap onto a fixed-size square canvas that is slightly
 * larger than the source, the resulting Bitmap dimensions never change. This ensures that
 * Jetpack Glance/RemoteViews does not try to rescale the image during animation frames.
 *
 * @param angle The current rotation angle in degrees (0-360).
 * @return A new, fixed-size Bitmap containing the rotated wheel.
 */
fun Bitmap.getSafeRotatedBitmap(angle: Float): Bitmap {

    // Calculate a fixed square size based on the diagonal. add a 10% buffer to ensure corners are never.
    val size = (maxOf(width, height) * 1.1).toInt()
    val newBitmap = createBitmap(size, size)
    val canvas = android.graphics.Canvas(newBitmap)
    val matrix = android.graphics.Matrix()

    // Apply rotation around the center of the ORIGINAL source bitmap.
    matrix.postRotate(angle, width / 2f, height / 2f)

    // Center the rotated bitmap
    val translateX = (size - width) / 2f
    val translateY = (size - height) / 2f
    matrix.postTranslate(translateX, translateY)

    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(this, matrix, paint)

    return newBitmap
}