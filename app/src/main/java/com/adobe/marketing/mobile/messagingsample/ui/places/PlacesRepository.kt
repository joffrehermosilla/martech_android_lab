package com.adobe.marketing.mobile.messagingsample.ui.places

import android.location.Location

interface PlacesRepository {

    fun getNearbyPlaces(

        location: Location

    )

}