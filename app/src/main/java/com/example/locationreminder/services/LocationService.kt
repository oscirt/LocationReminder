package com.example.locationreminder.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.data.storage.LocationClient
import com.example.domain.models.Note
import com.example.domain.usecase.CheckNoteUseCase
import com.example.domain.usecase.GetNotesUseCase
import com.example.locationreminder.R
import com.example.locationreminder.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.locationreminder.other.Constants.ACTION_STOP_SERVICE
import com.example.locationreminder.other.Constants.NOTIFICATION_ID
import com.example.locationreminder.other.Constants.REACHED_NOTIFICATION_ID
import com.example.locationreminder.presentation.MainActivity
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

    private lateinit var notes: List<Note>

    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var getNotesUseCase: GetNotesUseCase

    @Inject
    lateinit var checkNoteUseCase: CheckNoteUseCase

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
                    Log.d(TAG, "Else block: ${it.action}")
                }
            }
        }
        return START_STICKY
    }

    private fun start() {
        notification =
            NotificationCompat.Builder(this, getString(R.string.tracking_notification_channel_id))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Загрузка местоположения...")
                .setSmallIcon(R.drawable.baseline_location_on_24)
                .setOngoing(true)

        getNotesUseCase.execute()
            .onEach { list -> notes = list.filter { !it.isChecked } }
            .launchIn(serviceScope)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(2000L)
            .catch { it.printStackTrace() }
            .onEach { location ->
                lateinit var minNote: Note
                var minDistance = Double.MAX_VALUE
                for (i in notes) {
                    val distance = calculateDistance(
                        LatLng(location.latitude, location.longitude),
                        LatLng(i.latitude, i.longitude)
                    )
                    if (distance < minDistance) {
                        minDistance = distance
                        minNote = i
                    }
                }
                if (minDistance < 0.2) {
                    Log.d(TAG, "ALERT! ALERT! ALERT!")
                    reachedGeoZone(minNote)
                } else if (minDistance == Double.MAX_VALUE) {
                    updateNotification(
                        name = "Нет записей",
                        text = "Добавьте записи для отслеживания"
                    )
                } else {
                    updateNotification(
                        name = minNote.name,
                        text = "${(minDistance - 0.2).toString().take(4)} км"
                    )
                }
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

    private fun updateNotification(name: String, text: String) {
        val updatedNotification =
            notification
                .setContentTitle(name)
                .setContentText(text)
        notificationManager.notify(NOTIFICATION_ID, updatedNotification.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "binded")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        serviceScope.cancel()
    }

    private fun reachedGeoZone(note: Note) {
        serviceScope.launch {
            checkNoteUseCase.execute(note)
            val reachedNotification = NotificationCompat.Builder(applicationContext, getString(R.string.tracking_notification_channel_id))
                .setSmallIcon(R.drawable.baseline_flag_24)
            val contentIntent = Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("id", note.id)
                flags = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                REACHED_NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            reachedNotification
                .setContentTitle(note.name)
                .setContentText("Вы находитесь около ${note.name}")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(
                    R.drawable.baseline_flag_24,
                    "Подробнее",
                    pendingIntent
                ).priority = NotificationCompat.PRIORITY_MAX
            notificationManager.notify(REACHED_NOTIFICATION_ID, reachedNotification.build())
        }
    }

    private companion object {
        private const val TAG = "LocationService"
    }
}