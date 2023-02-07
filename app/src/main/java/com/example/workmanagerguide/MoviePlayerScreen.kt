package com.example.workmanagerguide

import android.net.Uri
import android.provider.Settings.Global.getString
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun MoviePlayerScreen(
    uri: String
) {
    Scaffold {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val mContext = LocalContext.current

            // Initializing ExoPLayer
            val mExoPlayer = remember(mContext) {
                ExoPlayer.Builder(mContext).build().apply {

                    val mediaItem = MediaItem.Builder()
                        .setUri(Uri.parse(uri))
                        .build()
                    setMediaItem(mediaItem)

                    playWhenReady = true
                    prepare()
                }
            }

            // Implementing ExoPlayer
            DisposableEffect(
                AndroidView(factory = {
                    StyledPlayerView(mContext).apply {
                        player = mExoPlayer
                    }
                })
            ) {
                onDispose { mExoPlayer.release() }
            }
        }
    }

}