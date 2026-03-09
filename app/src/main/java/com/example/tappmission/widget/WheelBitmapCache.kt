package com.example.tappmission.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.tappmission.data.responses.WheelAssets
import com.example.tappmission.utils.UrlPaths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Default fallback expiration used when the API returns 0 or null.
 * Expressed in milliseconds (30 minutes).
 */
private const val DEFAULT_CACHE_EXPIRATION_MS = 30 * 60 * 1000L

/**
 * Identifies each wheel layer and maps it to a local cache file name.
 *
 * Asset IDs are intentionally NOT hardcoded here. Instead, buildUrl()
 * reads the matching field from the API response's WheelAssets object,
 * so the server controls which files are displayed without a client update.
 */
enum class AssetType(val cacheFileName: String) {
    BACKGROUND("widget_bg.png"),
    FRAME("widget_frame.png"),
    SPIN("widget_spin.png"),
    WHEEL("widget_wheel.png");

    /**
     * Returns the asset path string for this layer from [assets],
     * or null if the server did not include it in the response.
     */
    fun getAssetId(assets: WheelAssets): String {
        val assetId = when (this) {
            BACKGROUND -> assets.background?.id
            FRAME -> assets.wheelFrame?.id
            SPIN -> assets.wheelSpin?.id
            WHEEL -> assets.wheelImage?.id
        }
        return assetId ?: ""
    }

    /**
     * Builds the full download URL using the asset path from [assets].
     * Returns null if the server did not provide an ID for this layer,
     * so the caller can skip the download gracefully.
     */
    fun buildUrl(assets: WheelAssets): String {
        val assetId = getAssetId(assets)
        return "${UrlPaths.BASE_URL}id=$assetId"
    }
}

/**
 * Returns a Bitmap for the given asset, using a disk cache.
 *
 * Cache validity is checked using the file's last-modified timestamp:
 *   valid = (file.lastModified + cacheExpiration) > currentTimeMillis
 *
 * If the cache is stale or missing, the Bitmap is downloaded from [url]
 * via Coil (with .allowHardware(false) — required for RemoteViews/widgets).
 *
 * If the download fails but a stale file exists on disk, the stale Bitmap
 * is returned as a safety fallback so the widget doesn't go blank offline.
 *
 * Note: [cacheExpiration] is assumed to be in milliseconds as provided by
 * the API's NetworkAttributes.cacheExpiration field.
 */
suspend fun getBitmapWithCache(
    context: Context,
    url: String,
    assetType: AssetType,
    cacheExpiration: Long
): Bitmap? = withContext(Dispatchers.IO) {
    val cacheFile = File(context.cacheDir, assetType.cacheFileName)
    val effectiveExpiration = cacheExpiration.takeIf { it > 0 } ?: DEFAULT_CACHE_EXPIRATION_MS
    val isCacheValid = cacheFile.exists() &&
            (cacheFile.lastModified() + effectiveExpiration > System.currentTimeMillis())

    if (isCacheValid) {
        return@withContext loadBitmapFromFile(cacheFile)
    }

    val downloaded = downloadBitmap(context, url)
    return@withContext if (downloaded != null) {
        saveBitmapToFile(downloaded, cacheFile)
        downloaded
    } else {
        // Network failed — return stale cache rather than showing nothing
        loadBitmapFromFile(cacheFile)
    }
}

/**
 * Reads a Bitmap from the disk cache without any network or expiry logic.
 * Called by WheelAppWidget.provideGlance — the widget only reads what the
 * receiver has already downloaded and saved.
 */
fun loadCachedBitmap(context: Context, assetType: AssetType): Bitmap? {
    return try {
        val file = File(context.cacheDir, assetType.cacheFileName)
        if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    } catch (e: IOException) {
        null
    }
}

/**
 * Downloads a Bitmap from [url] using Coil's ImageLoader.
 * allowHardware(false) is mandatory: hardware-backed bitmaps cannot be
 * serialised into RemoteViews, causing a crash on Android 8+.
 */
private suspend fun downloadBitmap(context: Context, url: String): Bitmap? {
    return try {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        (result as? SuccessResult)?.drawable?.toBitmap()
    } catch (e: Exception) {
        null
    }
}

/**
 * Persists a Bitmap to disk as PNG.
 * Errors are silently swallowed — a failed write simply means the next
 * call to getBitmapWithCache will attempt a fresh download.
 */
private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
    try {
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    } catch (e: IOException) {
        // Intentionally ignored
    }
}

private fun loadBitmapFromFile(file: File): Bitmap? {
    return try {
        if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    } catch (e: IOException) {
        null
    }
}
