package com.example.workmanagerguide

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

// Worker is a class that defines WorkManager Task
// Should be a long running task
// Should not be interrupted and run reliably even if app closes

// Worker will work independently of the application
// Even if the worker was started and the app was closed it will go on
// If the worker was scheduled on 10 minutes and device would restart it will execute the task.

@Suppress("BlockingMethodInNonBlockingContext")
@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val fileApi: FileApi
): CoroutineWorker(context, workerParams) {
// inherits from CoroutineWorker to use WorkMan with Coroutines
// parameters keep more info about specific worker

    // function that executes tasks
    // returns Result from WorkMan API
        // WorkMan uses the API to determine weather the task was Successful or Failed and then it may decide to retry
        // if download failes with response 5XX it will try again later
    override suspend fun doWork(): Result {
        startForegroundService() // notify start download
        delay(5000L)

        val link1 = "https://storage.googleapis.com/shaka-demo-assets/bbb-dark-truths/dash.mpd"
        val link2 = "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8"

        val response = fileApi.downloadVideo(link1)
        response.body()?.let { body -> // if body was not null
            return withContext(Dispatchers.IO) {
                val file = File(context.cacheDir, "bbb.mpd")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(body.bytes()) // byte ktore prisli ulozime do nami vytvorenej File
                    } catch(e: IOException) {
                        // if something went wrong we must return Result with or without data
                        return@withContext Result.failure(
                            workDataOf(
                                WorkerKeys.ERROR_MSG to e.localizedMessage // data is an error message
                            )
                        )
                    }
                }
                endForegroundService(file.toUri().toString())
                // if download and storage was successful return a Result.success with path to file
                Result.success(
//                    workDataOf(
//                        WorkerKeys.IMAGE_URI to file.toUri().toString()
//                    )
                )
            }
        }
        // if body is null
        if(!response.isSuccessful) {
            if(response.code().toString().startsWith("5")) {
                // if it was a server-side error retry
                return Result.retry()
            }
            return Result.failure(
                // if it was a client side error return Result with error message
                workDataOf(
                    WorkerKeys.ERROR_MSG to "Network error"
                )
            )
        }
        return Result.failure(
            // if a unknown error
            workDataOf(WorkerKeys.ERROR_MSG to "Unknown error")
        )
    }

    // Make notifications to show user what is the Task doing, WorkMan contains this behaviour
    // to easily show notification
    // In a long-run task WorkMan will execute it in a ForeGroundService (a service a user is aware of because they see the notifications)
    // WorkMan will handle it all, it just needs info from us.
    // To show notification we need to make a Notification Channel inside (class : Application() ) in this case DownloadApplication
    private suspend fun startForegroundService() {
        // Function to show the notification
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "download_channel") // our channel
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }

    private suspend fun endForegroundService(path: String) {
        // Function to show the notification
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "download_channel") // our channel
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText(path)
                    .setContentTitle("Download finished")
                    .build()
            )
        )
    }
}