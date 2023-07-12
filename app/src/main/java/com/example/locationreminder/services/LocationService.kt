package com.example.locationreminder.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.data.storage.LocationClient
import com.example.domain.models.Note
import com.example.domain.usecase.GetNotesUseCase
import com.example.locationreminder.R
import com.example.locationreminder.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.locationreminder.other.Constants.ACTION_STOP_SERVICE
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var getNotesUseCase: GetNotesUseCase

    @Inject
    lateinit var locationClient: LocationClient

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Log.d(TAG, "Started or resumed service")
                    start()
                }

                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Stop service")
                    stop()
                }

                else -> {

                }
            }
        }
        return START_STICKY
    }

    private fun start() {
        val notification =
            NotificationCompat.Builder(this, getString(R.string.tracking_notification_channel_id))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Загрузка местоположения...")
                .setSmallIcon(R.drawable.baseline_location_on_24)
                .setOngoing(true)

        var notes = listOf<Note>()

        serviceScope.launch { notes = getNotesUseCase.execute() }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(2000L)
            .catch { it.printStackTrace() }
            .onEach { location ->
                Log.d("TAG", "onLocationResult: $location")
                var minName = ""
                var minDistance = Double.MAX_VALUE
                for (i in notes) {
                    val distance = calculateDistance(
                        LatLng(location.latitude, location.longitude),
                        LatLng(i.latitude, i.longitude)
                    )
                    if (distance < minDistance) {
                        minDistance = distance
                        minName = i.name
                    }
                }
                val updatedNotification =
                    notification
                        .setContentTitle(minName)
                        .setContentText("${minDistance.toString().take(4)} км")
                notificationManager.notify(NOTIFICATION_ID, updatedNotification.build())
            }.launchIn(serviceScope)

        startForeground(NOTIFICATION_ID, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun calculateDistance(firstPoint: LatLng, secondPoint: LatLng): Double {
        val radius = 6378.1
        val firstRadiusLatitude = firstPoint.latitude * (Math.PI / 180)
        val secondRadiusLatitude = secondPoint.latitude * (Math.PI / 180)
        val diffLatitude = secondRadiusLatitude - firstRadiusLatitude
        val diffLongitude = (firstPoint.longitude - secondPoint.longitude) * (Math.PI / 180)

        return 2 * radius * asin(
            sqrt(
                sin(diffLatitude / 2) * sin(diffLatitude / 2) + cos(
                    firstRadiusLatitude
                ) * cos(secondRadiusLatitude) * sin(diffLongitude / 2) * sin(diffLongitude / 2)
            )
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private companion object {
        private const val TAG = "LocationService"

        private const val NOTIFICATION_ID = 123321
    }
}