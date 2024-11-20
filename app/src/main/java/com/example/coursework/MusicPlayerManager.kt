package com.example.coursework

import android.content.Context
import android.media.MediaPlayer

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var volume: Float = 1.0f // Default volume (40% of max)
    private const val MAX_VOLUME_MULTIPLIER = 500.0f // Boost the max volume

    fun startMusic(context: Context, resId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = true
        }
        setVolume(volume)
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun releaseMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setVolume(newVolume: Float) {
        // Cap the volume between 0.0f and 1.0f, then apply the multiplier
        volume = newVolume.coerceIn(0.0f, 1.0f) * MAX_VOLUME_MULTIPLIER
        mediaPlayer?.setVolume(volume, volume) // Set volume for both channels
    }
}
