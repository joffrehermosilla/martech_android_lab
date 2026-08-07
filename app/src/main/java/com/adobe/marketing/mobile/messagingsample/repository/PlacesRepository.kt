package com.adobe.marketing.mobile.messagingsample.repository

import com.adobe.marketing.mobile.messagingsample.adobe.AdobePlacesManager
import com.adobe.marketing.mobile.places.PlacesPOI

/**
 * Repositorio Enterprise para la gestión de puntos de interés (POIs).
 */
class PlacesRepository {

    fun getNearbyPOIs(
        lat: Double,
        lon: Double,
        onResult: (List<PlacesPOI>) -> Unit,
        onError: (String) -> Unit
    ) {
        AdobePlacesManager.getNearbyPointsOfInterest(
            latitude = lat,
            longitude = lon,
            limit = 10,
            onSuccess = { pois: List<PlacesPOI> -> onResult(pois) },
            onError = { error -> onError(error?.name ?: "Error desconocido") }
        )
    }

    fun getCurrentPOIs(onResult: (List<PlacesPOI>) -> Unit) {
        // Especificación explícita de tipo para evitar fallos de inferencia en Kotlin
        AdobePlacesManager.getCurrentPointsOfInterest { pois: List<PlacesPOI> ->
            onResult(pois)
        }
    }

    fun clearLocalData() {
        AdobePlacesManager.clear()
    }
}
