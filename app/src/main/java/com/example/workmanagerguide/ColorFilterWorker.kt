package com.example.workmanagerguide

import android.content.Context
import android.graphics.*
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Suppress("BlockingMethodInNonBlockingContext")
class ColorFilterWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    // Filter image color
    override suspend fun doWork(): Result {
        // Get file from DownloadWorker
        val imageFile = workerParams.inputData.getString(WorkerKeys.IMAGE_URI)
            ?.toUri()
            ?.toFile()
        delay(5000L)
        return imageFile?.let { file -> // File not null
            val bmp = BitmapFactory.decodeFile(file.absolutePath) // to work with image get bitmap
            val resultBmp = bmp.copy(bmp.config, true) // new image with color filter
            val paint = Paint()
            paint.colorFilter = LightingColorFilter(0x08FF04, 1) // apply filter
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(resultBmp, 0f, 0f, paint) // Draw new image

            // Store new file image
            withContext(Dispatchers.IO) {
                val resultImageFile = File(context.cacheDir, "new-image.jpg")
                val outputStream = FileOutputStream(resultImageFile)
                val successful = resultBmp.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )
                // If file successfully stored return Result with path to new file
                if(successful) {
                    Result.success(
                        workDataOf(
                            WorkerKeys.FILTER_URI to resultImageFile.toUri().toString()
                        )
                    )
                } else Result.failure() // if not stored return fail
            }
        } ?: Result.failure() // if file was null return fail
    }
}