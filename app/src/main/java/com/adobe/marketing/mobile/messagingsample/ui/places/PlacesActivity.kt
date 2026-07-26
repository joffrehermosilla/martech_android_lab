package com.adobe.marketing.mobile.messagingsample.ui.places

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.messagingsample.R
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeIdentityManager
import com.adobe.marketing.mobile.messagingsample.adobe.AdobePlacesManager
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardManager
import com.adobe.marketing.mobile.messagingsample.developer.DeveloperLocationRepository
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.places.PlacesPOI
import com.adobe.marketing.mobile.places.PlacesRequestError

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

        btnSearchPlaces.setOnClickListener {
            val lat = editLatitude.text.toString().toDoubleOrNull()
            val lon = editLongitude.text.toString().toDoubleOrNull()
            if (lat != null && lon != null) {
                searchPlaces(lat, lon)
            } else {
                Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show()
            }
        }
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
            // CORRECCIÓN: Usamos un constructor anónimo que hereda de PlacesPOI 
            // asegurando tipos correctos para radio (Int), peso (Int) y metadata (Map)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
