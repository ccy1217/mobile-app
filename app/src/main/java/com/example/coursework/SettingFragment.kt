package com.example.coursework

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switch = view.findViewById<Switch>(R.id.switch1)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar1)

        // Update switch state based on music status
        switch.isChecked = MusicPlayerManager.isPlaying()

        // Initialize SeekBar to reflect current volume
        seekBar.progress = (MusicPlayerManager.getVolume() * 100).toInt()

        // Toggle music playback
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MusicPlayerManager.startMusic(requireContext(), R.raw.music1)
                Toast.makeText(requireContext(), "Music started", Toast.LENGTH_SHORT).show()
            } else {
                MusicPlayerManager.stopMusic()
                Toast.makeText(requireContext(), "Music stopped", Toast.LENGTH_SHORT).show()
            }
        }

        // Update volume when SeekBar changes
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                MusicPlayerManager.setVolume(volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
