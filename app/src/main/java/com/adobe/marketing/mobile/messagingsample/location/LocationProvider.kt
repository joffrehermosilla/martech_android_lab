package com.adobe.marketing.mobile.messagingsample.location

import android.location.Location

interface LocationProvider {

    fun getCurrentLocation(
        callback: (Location?) -> Unit
    )

}