package com.adobe.marketing.mobile.messagingsample.ui.places

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.R
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeIdentityManager
import com.adobe.marketing.mobile.messagingsample.adobe.AdobePlacesManager
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardManager
import com.adobe.marketing.mobile.messagingsample.developer.DeveloperLocationRepository
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.places.PlacesPOI
import com.adobe.marketing.mobile.places.PlacesRequestError
import com.adobe.marketing.mobile.messagingsample.location.LocationManager
import com.adobe.marketing.mobile.messagingsample.permissions.PermissionManager
import android.location.Location
import com.adobe.marketing.mobile.messagingsample.location.GeofenceManager

/**
 * Actividad Enterprise para simulación de Adobe Places.
 * Soporta triggers de ubicación vinculados a CustomerID.
 */
class PlacesActivity : AppCompatActivity() {

    private lateinit var editLatitude: EditText
    private lateinit var editLongitude: EditText
    private lateinit var btnSearchPlaces: Button
    private lateinit var txtStatus: TextView
    private lateinit var recyclerPOIs: RecyclerView

    private lateinit var btnSimSanIsidro: Button
    private lateinit var btnSimMiraflores: Button
    private lateinit var btnSimJockey: Button
    private lateinit var btnSimAeropuerto: Button
    private lateinit var btnSimPlazaNorte: Button

    private lateinit var btnUseDeviceLocation: Button

    private lateinit var deviceLocationManager: LocationManager

    private lateinit var geofenceManager: GeofenceManager

    private var larcomarGeofenceRegistrationAttempted = false

    private var larcomarPoiForGeofence: PlacesPOI? = null

    private lateinit var btnStartLocationMonitoring: Button
    private lateinit var btnStopLocationMonitoring: Button

    private var lastAdobePlacesLocation: Location? = null

    private val minAdobePlacesDistanceMeters = 50f

    private val adapter = PlacesAdapter { poiItem ->
        simulatePoiEntry(poiItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        supportActionBar?.title = "Places Simulator - BCP CRM"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeViews()
        setupDeveloperPresets()
        deviceLocationManager = LocationManager(this)

        geofenceManager = GeofenceManager(applicationContext)

        btnUseDeviceLocation.setOnClickListener {
            useDeviceLocation()
        }

        btnStartLocationMonitoring.setOnClickListener {
            startLocationMonitoring()
        }

        btnStopLocationMonitoring.setOnClickListener {
            stopLocationMonitoring()
        }

        btnSearchPlaces.setOnClickListener {
            val lat = editLatitude.text.toString().toDoubleOrNull()
            val lon = editLongitude.text.toString().toDoubleOrNull()
            if (lat != null && lon != null) {
                searchPlaces(lat, lon)
            } else {
                Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.btnClearGeofences).setOnClickListener {

            geofenceManager.removeAllGeofences(
                onSuccess = {

                    larcomarGeofenceRegistrationAttempted = false

                    AdobeLogger.add(
                        "Geofence",
                        "Estado local reiniciado. Puede registrarse nuevamente.",
                        "SUCCESS"
                    )
                },
                onError = {
                    AdobeLogger.add(
                        "Geofence",
                        "No se pudo limpiar las geofences Android",
                        "ERROR"
                    )
                }
            )
        }
        findViewById<Button>(R.id.btnRegisterSavedGeofence)
            .setOnClickListener {

                val larcomar = larcomarPoiForGeofence

                if (larcomar == null) {
                    AdobeLogger.add(
                        "Geofence",
                        "No hay POI Larcomar guardado. Ejecuta Nearby primero.",
                        "ERROR"
                    )
                    return@setOnClickListener
                }

                val identifier = larcomar.identifier

                if (identifier.isNullOrBlank()) {
                    AdobeLogger.add(
                        "Geofence",
                        "POI guardado sin identifier. Registro cancelado.",
                        "ERROR"
                    )
                    return@setOnClickListener
                }

                AdobeLogger.add(
                    "Geofence",
                    "Registro aislado desde POI guardado -> " +
                            "name=${larcomar.name}, radius=${larcomar.radius}m",
                    "INFO"
                )

                larcomarGeofenceRegistrationAttempted = true

                geofenceManager.registerGeofence(
                    requestId = identifier,
                    latitude = larcomar.latitude,
                    longitude = larcomar.longitude,
                    radiusMeters = larcomar.radius.toFloat(),
                    onSuccess = {
                        AdobeLogger.add(
                            "Geofence",
                            "REGISTER SAVED POI SUCCESS",
                            "SUCCESS"
                        )
                    },
                    onError = {
                        larcomarGeofenceRegistrationAttempted = false

                        AdobeLogger.add(
                            "Geofence",
                            "REGISTER SAVED POI ERROR",
                            "ERROR"
                        )
                    }
                )
            }
        findViewById<Button>(R.id.btnGetCurrentPois)
            .setOnClickListener {

                AdobeLogger.add(
                    "Places",
                    "CURRENT ONLY REQUEST",
                    "INFO"
                )

                AdobePlacesManager.getCurrentPointsOfInterest { currentPois ->

                    AdobeLogger.add(
                        "Places",
                        "CURRENT ONLY RESULT -> count=${currentPois.size}",
                        "SUCCESS"
                    )

                    currentPois.forEach { poi ->
                        AdobeLogger.add(
                            "Places",
                            "CURRENT ONLY POI -> " +
                                    "name=${poi.name}, " +
                                    "inside=${poi.containsUser()}",
                            "INFO"
                        )
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        MobileCore.lifecycleStart(null)
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause()
    }

    private fun initializeViews() {
        editLatitude = findViewById(R.id.editLatitude)
        editLongitude = findViewById(R.id.editLongitude)
        btnSearchPlaces = findViewById(R.id.btnSearchPlaces)
        txtStatus = findViewById(R.id.txtStatus)
        recyclerPOIs = findViewById(R.id.recyclerPOIs)

        btnSimSanIsidro = findViewById(R.id.btnSimSanIsidro)
        btnSimMiraflores = findViewById(R.id.btnSimMiraflores)
        btnSimJockey = findViewById(R.id.btnSimJockey)
        btnSimAeropuerto = findViewById(R.id.btnSimAeropuerto)
        btnSimPlazaNorte = findViewById(R.id.btnSimPlazaNorte)

        recyclerPOIs.layoutManager = LinearLayoutManager(this)
        recyclerPOIs.adapter = adapter
        btnUseDeviceLocation =
            findViewById(R.id.btnUseDeviceLocation)

        btnStartLocationMonitoring =
            findViewById(R.id.btnStartLocationMonitoring)

        btnStopLocationMonitoring =
            findViewById(R.id.btnStopLocationMonitoring)
    }

    private fun setupDeveloperPresets() {
        DeveloperLocationRepository.places.forEach { location ->
            when (location.name) {
                "BCP San Isidro" -> btnSimSanIsidro.setOnClickListener {
                    setCoordinatesAndSearch(location.latitude, location.longitude, location.name)
                }
                "Larcomar" -> btnSimMiraflores.setOnClickListener {
                    setCoordinatesAndSearch(location.latitude, location.longitude, "Miraflores")
                }
                "Jockey Plaza" -> btnSimJockey.setOnClickListener {
                    setCoordinatesAndSearch(location.latitude, location.longitude, location.name)
                }
                "Aeropuerto Jorge Chavez" -> btnSimAeropuerto.setOnClickListener {
                    setCoordinatesAndSearch(location.latitude, location.longitude, location.name)
                }
                "Plaza Norte" -> btnSimPlazaNorte.setOnClickListener {
                    setCoordinatesAndSearch(location.latitude, location.longitude, location.name)
                }
            }
        }
    }

    private fun setCoordinatesAndSearch(lat: Double, lon: Double, name: String) {
        editLatitude.setText(lat.toString())
        editLongitude.setText(lon.toString())
        AdobeLogger.add("SimuladorGPS", "Simulando: $name", "INFO")
        searchPlaces(lat, lon)
    }

    private fun searchPlaces(lat: Double, lon: Double) {
        txtStatus.text = "Consultando Adobe Places..."
        
        AdobePlacesManager.getNearbyPointsOfInterest(
            latitude = lat,
            longitude = lon,
            limit = 10,
            onSuccess = { pois ->
                val items = pois.map { poi ->
                    PlaceItem(
                        name = poi.name ?: "Sin nombre",
                        identifier = poi.identifier ?: "-",
                        distance = 0.0,
                        inside = poi.containsUser()
                    )
                }
                registerLarcomarGeofenceIfNeeded(pois)
                AdobePlacesManager.getLastKnownLocation {
                    // No necesitamos hacer nada aquí todavía.
                    // AdobePlacesManager ya escribe el resultado en AdobeLogger.
                    AdobePlacesManager.getCurrentPointsOfInterest { currentPois ->
                        // Por ahora solo usamos AdobeLogger.
                    }
                }

                runOnUiThread {
                    adapter.submitList(items)
                    txtStatus.text = "${items.size} POIs encontrados"
                    DashboardManager.placesConnected.postValue(true)
                }
            },
            onError = { error ->
                runOnUiThread {
                    txtStatus.text = "Error: ${error?.name ?: "Desconocido"}"
                }
            }
        )
    }

    private fun simulatePoiEntry(poiItem: PlaceItem) {
        AdobeIdentityManager.getActiveIdentifier { activeId ->
            val dummyPoi = object : PlacesPOI(
                poiItem.identifier, 
                poiItem.name, 
                0.0, 
                0.0, 
                100, 
                "SimulatedLib", 
                1, 
                HashMap<String, String>()
            ) {}
            
            AdobePlacesManager.simulatePOIClick(dummyPoi, activeId)
            
            runOnUiThread {
                Toast.makeText(this, "Trigger XDM enviado para: ${poiItem.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun useDeviceLocation() {

        if (!PermissionManager.hasLocationPermission(this)) {

            PermissionManager.requestLocationPermission(this)

            return
        }

        loadDeviceLocation()
    }

    private fun loadDeviceLocation() {

        txtStatus.text = "Obteniendo ubicación del dispositivo..."

        deviceLocationManager.getCurrentLocation { location ->

            if (location == null) {

                runOnUiThread {
                    txtStatus.text = "No se pudo obtener la ubicación"

                    AdobeLogger.add(
                        "AndroidLocation",
                        "Device Location -> null",
                        "ERROR"
                    )
                }

                return@getCurrentLocation
            }

            val lat = location.latitude
            val lon = location.longitude

            AdobeLogger.add(
                "AndroidLocation",
                "Device Location -> lat=$lat, lon=$lon",
                "INFO"
            )

            runOnUiThread {

                editLatitude.setText(lat.toString())
                editLongitude.setText(lon.toString())

                searchPlaces(lat, lon)
            }
        }
    }

    private fun startLocationMonitoring() {

        if (!PermissionManager.hasLocationPermission(this)) {

            PermissionManager.requestLocationPermission(this)

            return
        }
        lastAdobePlacesLocation = null

        txtStatus.text = "Monitoreando ubicación..."

        AdobeLogger.add(
            "AndroidLocation",
            "GPS Monitoring -> START",
            "INFO"
        )

        deviceLocationManager.startLocationUpdates { location ->

            val lat = location.latitude
            val lon = location.longitude

            AdobeLogger.add(
                "AndroidLocation",
                "GPS UPDATE -> lat=$lat, lon=$lon",
                "INFO"
            )

            runOnUiThread {
                editLatitude.setText(lat.toString())
                editLongitude.setText(lon.toString())

                txtStatus.text =
                    "GPS activo: $lat, $lon"
            }

            maybeRefreshAdobePlaces(location)
        }
    }

    private fun stopLocationMonitoring() {

        deviceLocationManager.stopLocationUpdates()

        AdobeLogger.add(
            "AndroidLocation",
            "GPS Monitoring -> STOP",
            "INFO"
        )

        txtStatus.text = "Monitoreo GPS detenido"
    }

    private fun maybeRefreshAdobePlaces(location: Location) {

        val previousLocation = lastAdobePlacesLocation

        val shouldRefresh =
            previousLocation == null ||
                    previousLocation.distanceTo(location) >= minAdobePlacesDistanceMeters

        if (!shouldRefresh) {
            return
        }

        lastAdobePlacesLocation = Location(location)

        AdobeLogger.add(
            "Places",
            "AUTO REFRESH -> lat=${location.latitude}, lon=${location.longitude}",
            "INFO"
        )

        searchPlaces(
            location.latitude,
            location.longitude
        )
    }

    private fun registerLarcomarGeofenceIfNeeded(
        pois: List<com.adobe.marketing.mobile.places.PlacesPOI>
    ) {

        if (larcomarGeofenceRegistrationAttempted) {
            return
        }

        val larcomar = pois.firstOrNull { poi ->
            poi.name.equals(
                "C.C. Larcomar",
                ignoreCase = true
            )
        }

        if (larcomar == null) {

            AdobeLogger.add(
                "Geofence",
                "C.C. Larcomar no encontrado entre Nearby POIs",
                "WARN"
            )

            return
        }

        larcomarPoiForGeofence = larcomar

        AdobeLogger.add(
            "Geofence",
            "POI Larcomar guardado para prueba aislada",
            "INFO"
        )

        val identifier = larcomar.identifier

        if (identifier.isNullOrBlank()) {

            AdobeLogger.add(
                "Geofence",
                "Larcomar sin identifier. Registro cancelado.",
                "ERROR"
            )

            return
        }

        larcomarGeofenceRegistrationAttempted = true

        AdobeLogger.add(
            "Geofence",
            "POI seleccionado desde Adobe -> " +
                    "name=${larcomar.name}, " +
                    "radius=${larcomar.radius}m",
            "INFO"
        )

        geofenceManager.registerGeofence(
            requestId = identifier,
            latitude = larcomar.latitude,
            longitude = larcomar.longitude,
            radiusMeters = larcomar.radius.toFloat(),
            onSuccess = {

                AdobeLogger.add(
                    "Geofence",
                    "Larcomar listo para monitoreo Android",
                    "SUCCESS"
                )
            },
            onError = {

                larcomarGeofenceRegistrationAttempted = false

                AdobeLogger.add(
                    "Geofence",
                    "No se pudo registrar Larcomar",
                    "ERROR"
                )
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode ==
            PermissionManager.LOCATION_PERMISSION_REQUEST
        ) {

            if (PermissionManager.hasLocationPermission(this)) {

                loadDeviceLocation()

            } else {

                AdobeLogger.add(
                    "AndroidLocation",
                    "Permiso de ubicación denegado",
                    "ERROR"
                )

                Toast.makeText(
                    this,
                    "Se requiere permiso de ubicación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        deviceLocationManager.stopLocationUpdates()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}