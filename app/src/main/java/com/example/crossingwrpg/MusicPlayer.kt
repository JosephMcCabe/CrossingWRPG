package com.example.crossingwrpg

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.net.toUri

object MusicPlayer {
    private var player: ExoPlayer? = null

    fun preparePlayer(context: Context) {
        if (player == null) {
            player = ExoPlayer.Builder(context.applicationContext).build()
            player?.prepare()
        }
    }

    fun changeSong(songName: String) {
        if (songName.toUri() != player?.currentMediaItem) {
            val victory1Uri = ("android.resource://com.example.crossingwrpg/raw/$songName").toUri()
            val mediaItem = MediaItem.fromUri(victory1Uri)
            player?.setMediaItem(mediaItem)
            player?.prepare()
        }
    }

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun loop() {
            player?.repeatMode = REPEAT_MODE_ALL
    }

    fun unloop() {
        player?.repeatMode = REPEAT_MODE_OFF
    }

    fun free() {
        player?.release()
        player = null
    }
}
