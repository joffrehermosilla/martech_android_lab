package com.adobe.marketing.mobile.messagingsample.adobe

import android.location.Location
import com.adobe.marketing.mobile.AdobeCallback
import com.adobe.marketing.mobile.Places
import com.adobe.marketing.mobile.places.PlacesPOI
import com.adobe.marketing.mobile.places.PlacesRequestError
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger

/**
 * Gestor avanzado para Adobe Places Service.
 * Homologado para Real-Time CDP y AJO mediante triggers de ubicación.
 */
object AdobePlacesManager {

    fun getNearbyPointsOfInterest(
        latitude: Double,
        longitude: Double,
        limit: Int = 10,
        onSuccess: (List<PlacesPOI>) -> Unit,
        onError: (PlacesRequestError?) -> Unit
    ) {
        val location = Location("SimulatedProvider").apply {
            this.latitude = latitude
            this.longitude = longitude
        }

        AdobeLogger.add(
            "Places",
            "Nearby REQUEST -> lat=$latitude, lon=$longitude",
            "INFO"
        )

        Places.getNearbyPointsOfInterest(
            location,
            limit,
            AdobeCallback { pois ->
                val finalPois = pois ?: emptyList()

                AdobeLogger.add(
                    "Places",
                    "Nearby SUCCESS -> lat=$latitude, lon=$longitude | POIs=${finalPois.size}",
                    "SUCCESS"
                )

                finalPois.forEach { poi ->

                    AdobeLogger.add(
                        "Places",
                        "Nearby POI -> name=${poi.name}, " +
                                "lat=${poi.latitude}, " +
                                "lon=${poi.longitude}, " +
                                "radius=${poi.radius}m, " +
                                "inside=${poi.containsUser()}",
                        "INFO"
                    )
                }

                onSuccess(finalPois)
            },
            AdobeCallback { error ->
                AdobeLogger.add(
                    "Places",
                    "Nearby ERROR -> lat=$latitude, lon=$longitude | ${error?.name}",
                    "ERROR"
                )

                onError(error)
            }
        )
    }

    /**
     * Recupera los POIs en los que el usuario se encuentra actualmente.
     */
    fun getCurrentPointsOfInterest(
        callback: (List<PlacesPOI>) -> Unit
    ) {
        Places.getCurrentPointsOfInterest(
            AdobeCallback { pois ->

                val finalPois = pois ?: emptyList()

                AdobeLogger.add(
                    "Places",
                    "Current POIs -> count=${finalPois.size}",
                    "INFO"
                )

                finalPois.forEach { poi ->
                    AdobeLogger.add(
                        "Places",
                        "Current POI -> name=${poi.name}, id=${poi.identifier}, inside=${poi.containsUser()}",
                        "INFO"
                    )
                }

                callback(finalPois)
            }
        )
    }

    /**
     * Simula la entrada a un POI para gatillar eventos en AJO/CDP.
     */
    fun simulatePOIClick(poi: PlacesPOI, customerId: String = "") {
        AdobeLogger.add("Places", "Simulando interacción con POI: ${poi.name}", "INFO")
        
        AdobeJourneyManager.sendInteractionEvent(
            customerId = customerId,
            label = "POI Entry: ${poi.name}",
            group = "LocationServices",
            category = "AdobePlaces.POI",
            component = poi.identifier ?: "unknown"
        )
    }

    fun getLastKnownLocation(
        callback: (Location?) -> Unit
    ) {
        Places.getLastKnownLocation(
            AdobeCallback { location ->

                if (location != null) {
                    AdobeLogger.add(
                        "Places",
                        "LastKnownLocation -> lat=${location.latitude}, lon=${location.longitude}",
                        "INFO"
                    )
                } else {
                    AdobeLogger.add(
                        "Places",
                        "LastKnownLocation -> null",
                        "WARN"
                    )
                }

                callback(location)
            }
        )
    }


    fun clear() {
        Places.clear()
        AdobeLogger.add("Places", "Caché de ubicaciones limpia", "WARN")
    }
}
