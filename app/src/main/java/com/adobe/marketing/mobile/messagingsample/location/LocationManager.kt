package com.adobe.marketing.mobile.messagingsample.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationManager(
    context: Context
) : LocationProvider {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var continuousLocationCallback: LocationCallback? = null

    /**
     * Obtiene una única ubicación actual.
     */
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(
        callback: (Location?) -> Unit
    ) {
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    /**
     * Inicia seguimiento continuo de ubicación.
     *
     * Pensado inicialmente para pruebas foreground
     * con Android Emulator Routes.
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        onLocationChanged: (Location) -> Unit
    ) {

        // Evita registrar callbacks duplicados.
        stopLocationUpdates()

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000L
            )
                .setMinUpdateIntervalMillis(1000L)
                .setMinUpdateDistanceMeters(5f)
                .build()

        val callback = object : LocationCallback() {

            override fun onLocationResult(
                locationResult: LocationResult
            ) {
                locationResult.locations.forEach { location ->
                    onLocationChanged(location)
                }
            }
        }

        continuousLocationCallback = callback

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
    }

    /**
     * Detiene el seguimiento continuo.
     */
    fun stopLocationUpdates() {

        continuousLocationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }

        continuousLocationCallback = null
    }
}