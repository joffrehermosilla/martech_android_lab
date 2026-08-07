package com.adobe.marketing.mobile.messagingsample.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.adobe.marketing.mobile.Places

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val geofencingEvent =
            GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {

            AdobeLogger.add(
                "Geofence",
                "Evento recibido pero GeofencingEvent=null",
                "ERROR"
            )

            return
        }

        if (geofencingEvent.hasError()) {

            val errorCode = geofencingEvent.errorCode

            AdobeLogger.add(
                "Geofence",
                "Geofence ERROR -> code=$errorCode, " +
                        "message=${GeofenceStatusCodes.getStatusCodeString(errorCode)}",
                "ERROR"
            )

            return
        }

        val transition =
            when (geofencingEvent.geofenceTransition) {

                Geofence.GEOFENCE_TRANSITION_ENTER ->
                    "ENTER"

                Geofence.GEOFENCE_TRANSITION_EXIT ->
                    "EXIT"

                Geofence.GEOFENCE_TRANSITION_DWELL ->
                    "DWELL"

                else ->
                    "UNKNOWN(${geofencingEvent.geofenceTransition})"
            }

        val ids =
            geofencingEvent.triggeringGeofences
                ?.map { it.requestId }
                ?: emptyList()

        AdobeLogger.add(
            "Geofence",
            "TRANSITION -> $transition | ids=$ids",
            "SUCCESS"
        )

        if (
            geofencingEvent.geofenceTransition ==
            Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofencingEvent.geofenceTransition ==
            Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            Places.processGeofenceEvent(
                geofencingEvent
            )

            AdobeLogger.add(
                "Places",
                "processGeofenceEvent INVOKED -> " +
                        "transition=$transition | ids=$ids",
                "SUCCESS"
            )
        }
    }
}