package com.adobe.marketing.mobile.messagingsample.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {

    const val LOCATION_PERMISSION_REQUEST = 2000

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasLocationPermission(context: Context): Boolean {

        return permissions.all {

            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED

        }

    }

    fun requestLocationPermission(activity: Activity) {

        ActivityCompat.requestPermissions(

            activity,

            permissions,

            LOCATION_PERMISSION_REQUEST

        )

    }

}