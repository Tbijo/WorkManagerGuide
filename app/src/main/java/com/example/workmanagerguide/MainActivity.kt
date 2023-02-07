package com.example.workmanagerguide

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.*
import coil.compose.rememberImagePainter
import com.example.workmanagerguide.ui.theme.WorkManagerGuideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call DownloadWorker once
        // for periodic call every 5 hours - PeriodicWorkRequestBuilder<DownloadWorker>(Duration.ofHours(5))
        /*val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            // set conditions which need to filled in order that Worker will execute its Task
            .setConstraints(
                Constraints.Builder()
                    // Make sure device has Internet Access (Not Charging, Charging, Battery not low,...)
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()
        // There are parameters to set to the Worker like setInitialDelay, setBackoffCriteria - (specify the behaviour for retrying), ...

        // Call ColorFilterWorker
        val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
            .build()

        // Call WorkMan
        val workManager = WorkManager.getInstance(applicationContext)
        setContent {
            WorkManagerGuideTheme {
                // object that contains info about a specific Worker
                // this contains all of our scheduled WorkManager tasks
                val workInfos = workManager
                    .getWorkInfosForUniqueWorkLiveData("download") // one instance of a task running - one download task
                    .observeAsState() // compose state
                    .value

                // Get Uris do Downloaded and Filtered Images

                // get work info that is related to Download Request
                // when key1 changes it will refetch this remember block
                val downloadInfo = remember(key1 = workInfos) {
                    workInfos?.find { it.id == downloadRequest.id }
                }

                // get work info that is related to Filtered Request
                val filterInfo = remember(key1 = workInfos) {
                    workInfos?.find { it.id == colorFilterRequest.id }
                }
                // From these infos we can get the Uris and error messages

                val imageUri by derivedStateOf { // will cache the result of this block
                    // refetch this expression here
                    // save it in imageUri whenever it changes
                    // and notify all of its (imageUri) observers (composables) that there is a new Result

                    // Get Down Image Uri
                    val downloadUri = downloadInfo?.outputData?.getString(WorkerKeys.IMAGE_URI)
                        ?.toUri()

                    // Get Filtered Image Uri
                    val filterUri = filterInfo?.outputData?.getString(WorkerKeys.FILTER_URI)
                        ?.toUri()

                    // return current Uri to display (1. Down, 2. Filt)
                    // If filterUri == null then downloadUri else filterUri
                    // Download Uri should be accessible
                    filterUri ?: downloadUri
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageUri?.let { uri ->
                        Image(
                            painter = rememberImagePainter( // From Coil
                                data = uri
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(
                        onClick = {
                            // Launch Download Worker
                            workManager
                                .beginUniqueWork(
                                    "download",
                                    // What should happen if there is a work running with this unique name
                                    // There can not be 2 therefore apply policy
                                    // APPEND - when first is finished the second goes
                                    // REPLACE - replace current work with new one
                                    ExistingWorkPolicy.KEEP, // ignore second one wait for first one finish
                                    downloadRequest // work request
                                )
                                //.then(colorFilterRequest) // specify the next Request/Task
                                .enqueue()
                        },
                        // only enable if work is not running
                        enabled = downloadInfo?.state != WorkInfo.State.RUNNING
                    ) {
                        Text(text = "Start download")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when(downloadInfo?.state) {
                        WorkInfo.State.RUNNING -> Text("Downloading...")
                        WorkInfo.State.SUCCEEDED -> Text("Download succeeded")
                        WorkInfo.State.FAILED -> Text("Download failed")
                        WorkInfo.State.CANCELLED -> Text("Download cancelled")
                        WorkInfo.State.ENQUEUED -> Text("Download enqueued")
                        WorkInfo.State.BLOCKED -> Text("Download blocked")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when(filterInfo?.state) {
                        WorkInfo.State.RUNNING -> Text("Applying filter...")
                        WorkInfo.State.SUCCEEDED -> Text("Filter succeeded")
                        WorkInfo.State.FAILED -> Text("Filter failed")
                        WorkInfo.State.CANCELLED -> Text("Filter cancelled")
                        WorkInfo.State.ENQUEUED -> Text("Filter enqueued")
                        WorkInfo.State.BLOCKED -> Text("Filter blocked") // Initially it is always blocked
                    }
                }
            }
        }*/

        setContent {
            WorkManagerGuideTheme {
                val files = cacheDir.listFiles()

                files?.filter { it.canRead() && it.isFile }

                if (files != null) {
                    val path = files[2].toUri().path
                    println(path)
                    MoviePlayerScreen(uri = path!!)
                } else {
                    println("EMPTY")
                }
            }
        }
    }
}