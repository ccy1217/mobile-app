package com.example.coursework

import android.annotation.SuppressLint
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
    private val PREF_MUSIC_PLAYING = "pref_music_playing"
    private val PREF_MUSIC_VOLUME = "musicVolume"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("com.example.coursework", Context.MODE_PRIVATE)

        val switch = view.findViewById<Switch>(R.id.switch1)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar1)

        val isMusicPlaying = sharedPreferences.getBoolean(PREF_MUSIC_PLAYING, false)
        val savedVolume = sharedPreferences.getFloat(PREF_MUSIC_VOLUME, 0.4f)

        switch.isChecked = isMusicPlaying
        seekBar.progress = (savedVolume * 100).toInt()

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MusicPlayerManager.startMusic(requireContext(), R.raw.music)
                showToast("Music started")
            } else {
                MusicPlayerManager.stopMusic()
                showToast("Music stopped")
            }
            sharedPreferences.edit().apply {
                putBoolean(PREF_MUSIC_PLAYING, isChecked)
                apply()
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                MusicPlayerManager.setVolume(volume)
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
