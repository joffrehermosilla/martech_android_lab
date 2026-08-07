package com.adobe.marketing.mobile.messagingsample.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceManager(
    private val context: Context
) {

    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {

        val intent =
            Intent(
                context,
                GeofenceBroadcastReceiver::class.java
            )

        var flags =
            PendingIntent.FLAG_UPDATE_CURRENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags or PendingIntent.FLAG_MUTABLE
        }

        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flags
        )
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence(
        requestId: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        if (!hasRequiredPermissions()) {

            AdobeLogger.add(
                "Geofence",
                "Registro cancelado: faltan permisos de ubicación",
                "ERROR"
            )

            return
        }

        val geofence =
            Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(
                    latitude,
                    longitude,
                    radiusMeters
                )
                .setExpirationDuration(
                    Geofence.NEVER_EXPIRE
                )
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build()

        val request =
            GeofencingRequest.Builder()
                .setInitialTrigger(
                    GeofencingRequest.INITIAL_TRIGGER_ENTER
                )
                .addGeofence(geofence)
                .build()

        AdobeLogger.add(
            "Geofence",
            "Registrando -> id=$requestId | " +
                    "lat=$latitude | lon=$longitude | " +
                    "radius=${radiusMeters}m",
            "INFO"
        )

        geofencingClient
            .addGeofences(
                request,
                geofencePendingIntent
            )
            .addOnSuccessListener {

                AdobeLogger.add(
                    "Geofence",
                    "REGISTER SUCCESS -> id=$requestId | radius=${radiusMeters}m",
                    "SUCCESS"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                AdobeLogger.add(
                    "Geofence",
                    "REGISTER ERROR -> ${exception.javaClass.simpleName}: ${exception.message}",
                    "ERROR"
                )

                onError(exception)
            }
    }

    private fun hasRequiredPermissions(): Boolean {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val backgroundGranted =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
            ) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        return fineGranted && backgroundGranted
    }

    fun removeAllGeofences(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        AdobeLogger.add(
            "Geofence",
            "Eliminando geofences asociadas al PendingIntent...",
            "INFO"
        )

        geofencingClient
            .removeGeofences(
                geofencePendingIntent
            )
            .addOnSuccessListener {

                AdobeLogger.add(
                    "Geofence",
                    "REMOVE ALL SUCCESS",
                    "SUCCESS"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                AdobeLogger.add(
                    "Geofence",
                    "REMOVE ALL ERROR -> " +
                            "${exception.javaClass.simpleName}: ${exception.message}",
                    "ERROR"
                )

                onError(exception)
            }
    }
}