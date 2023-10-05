package com.example.locationreminder.other

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.locationreminder.R

class LocationPermissionSettingsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.settings_location_permission_text))
            .setPositiveButton(R.string.settings_location_permission_positive) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
            .setNegativeButton(R.string.settings_location_permission_negative) { _, _ ->
                dismiss()
            }
            .create()

    companion object {
        const val TAG = "LocationPermissionSettingsDialogFragment"
    }
}