package com.example.locationreminder.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.locationreminder.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
        val appbarConfiguration = AppBarConfiguration(setOf(R.id.notesListFragment, R.id.locationPermissionFragment))
        NavigationUI.setupActionBarWithNavController(
            activity = this,
            navController = navController,
            appbarConfiguration
        )

        notificationManager = ContextCompat.getSystemService(
            this, NotificationManager::class.java
        ) as NotificationManager

        createNotificationChannel(
            getString(R.string.tracking_notification_channel_id),
            getString(R.string.tracking_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotificationChannel(
            getString(R.string.reached_notification_channel_id),
            getString(R.string.reached_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(
                        this,
                        "Без этого разрешения нельзя показывать уведомления о местоположении",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        if (Build.VERSION.SDK_INT > 32 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                importance
            ).apply {
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(false)
                description = "Отслеживание местоположения"
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}