package com.example.coursework

import android.content.Context
import android.media.MediaPlayer

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var volume: Float = 0.4f // Default volume

    // Start music playback
    fun startMusic(context: Context, resId: Int) {
        // If mediaPlayer is not null, stop and reset it first
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.reset()
        }

        // Create a new MediaPlayer if it's null
        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()
    }

    // Stop the music
    fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.reset()
        }
        mediaPlayer = null
    }

    // Check if music is playing
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    // Set the music volume
    fun setVolume(newVolume: Float) {
        volume = newVolume
        mediaPlayer?.setVolume(volume, volume)
    }

    // Get the current volume
    fun getVolume(): Float {
        return volume
    }

    // Release resources when done
    fun releaseMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
