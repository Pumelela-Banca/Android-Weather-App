package com.example.myapplication

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.example.myapplication.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ApiKeyDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.get_api_key, null, false)
        val etApiKey = view.findViewById<TextInputEditText>(R.id.etApiKey)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("API Key Required")
            .setMessage("Paste your OpenWeather API key to continue.")
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("Save") { dialog, _ ->
                val key = etApiKey.text?.toString()?.trim().orEmpty()
                if (key.isNotEmpty()) {
                    // Return key to the Activity via Fragment Result API
                    setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_API to key))
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    companion object {
        const val REQUEST_KEY = "ApiKeyDialogRequest"
        const val BUNDLE_KEY_API = "apiKey"
        fun newInstance() = ApiKeyDialogFragment()
    }
}
