package com.example.coursework

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "MusicPreferences"
    private val PREF_MUSIC_PLAYING = "isMusicPlaying"
    private val PREF_MUSIC_VOLUME = "musicVolume"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Get references to the UI elements
        val switch = view.findViewById<Switch>(R.id.switch1)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar1)

        // Restore music state and volume from SharedPreferences
        val isMusicPlaying = sharedPreferences.getBoolean(PREF_MUSIC_PLAYING, false)
        val savedVolume = sharedPreferences.getFloat(PREF_MUSIC_VOLUME, 0.4f) // Default volume is 0.4f

        // Set the switch state based on saved preference
        switch.isChecked = isMusicPlaying
        // Set the SeekBar progress based on saved volume preference
        seekBar.progress = (savedVolume * 100).toInt()

        // Toggle music playback and save the state
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Start the music if the switch is on
                MusicPlayerManager.startMusic(requireContext(), R.raw.music1)
                showToast("Music started")
            } else {
                // Stop the music if the switch is off
                MusicPlayerManager.stopMusic()
                showToast("Music stopped")
            }

            // Save music state (playing or not) to SharedPreferences
            sharedPreferences.edit().apply {
                putBoolean(PREF_MUSIC_PLAYING, isChecked)
                apply()
            }
        }

        // Update volume when SeekBar changes
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Convert progress to volume (0.0f to 1.0f)
                val volume = progress / 100f
                // Set the volume in the MusicPlayerManager
                MusicPlayerManager.setVolume(volume)

                // Save the volume state to SharedPreferences
                sharedPreferences.edit().apply {
                    putFloat(PREF_MUSIC_VOLUME, volume)
                    apply()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showToast(message: String) {
        // Show a toast message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
