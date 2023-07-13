package com.example.data.storage.location

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.example.data.storage.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (!LocationUtility.hasLocationPermission(context)) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val request = LocationRequest.Builder(interval)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch {
                            send(location)
                        }
                    }
                }
            }

            if (!LocationUtility.hasLocationPermission(context)) {
                throw LocationClient.LocationException("Missing location permission")
            }
            try {
                client.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Log.e(TAG, "getLocationUpdates: ${e.message}", e)
            }

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    private companion object {
        private const val TAG = "DefaultLocationClient"
    }
}