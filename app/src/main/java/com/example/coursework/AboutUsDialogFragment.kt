package com.example.coursework

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AboutUsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fragment_about_us_dialog, null)

        val cancelButton: Button = view.findViewById(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            dismiss() // Close the dialog when Cancel is clicked
        }

        builder.setView(view)
        return builder.create()
    }
}
