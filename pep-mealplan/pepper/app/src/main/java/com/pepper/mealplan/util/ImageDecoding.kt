package com.pepper.mealplan.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun decodeSampledBitmap(
    bytes: ByteArray,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    val bounds = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)

    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight)
        inPreferredConfig = Bitmap.Config.RGB_565
    }

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        var halfHeight = height / 2
        var halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight &&
            (halfWidth / inSampleSize) >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }

    return inSampleSize.coerceAtLeast(1)
}
