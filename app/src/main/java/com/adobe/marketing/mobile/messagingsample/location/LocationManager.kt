package com.adobe.marketing.mobile.messagingsample.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices

class LocationManager(
    context: Context
) : LocationProvider {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(
        callback: (Location?) -> Unit
    ) {

        fusedLocationClient.lastLocation

            .addOnSuccessListener {

                callback(it)

            }

            .addOnFailureListener {

                callback(null)

            }

    }

}