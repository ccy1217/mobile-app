package com.example.coursework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switch = view.findViewById<Switch>(R.id.switch1)

        // Set switch state based on music playing status
        switch.isChecked = MusicPlayerManager.isPlaying()

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MusicPlayerManager.startMusic(requireContext(), R.raw.music1)
                Toast.makeText(requireContext(), "Music is looping", Toast.LENGTH_SHORT).show()
            } else {
                MusicPlayerManager.stopMusic()
                Toast.makeText(requireContext(), "Music stopped", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
