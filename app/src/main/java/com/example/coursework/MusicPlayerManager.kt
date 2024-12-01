package com.example.coursework

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var volume: Float = 1.0f // Default volume (range: 0.0f to 1.0f)
    private const val MAX_VOLUME_MULTIPLIER = 3.0f // Volume cap for normal MediaPlayer

    fun startMusic(context: Context, resId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = true
            setupLoudnessEnhancer()
        }
        setVolume(volume)
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        releaseEnhancer()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun releaseMusic() {
        mediaPlayer?.release()
        releaseEnhancer()
        mediaPlayer = null
    }

    fun setVolume(newVolume: Float) {
        // Cap the volume between 0.0f and 1.0f
        volume = newVolume.coerceIn(0.0f, 1.0f) * MAX_VOLUME_MULTIPLIER
        mediaPlayer?.setVolume(volume, volume) // Adjust the MediaPlayer volume
    }

    private fun setupLoudnessEnhancer() {
        mediaPlayer?.audioSessionId?.let { sessionId ->
            loudnessEnhancer = LoudnessEnhancer(sessionId)
            loudnessEnhancer?.setTargetGain(1000) // Boost up to 10dB (adjust as needed)
            loudnessEnhancer?.enabled = true
        }
    }

    private fun releaseEnhancer() {
        loudnessEnhancer?.release()
        loudnessEnhancer = null
    }
}
