package com.adobe.marketing.mobile.messagingsample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.Messaging
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeIdentityManager
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeJourneyManager
import com.adobe.marketing.mobile.messagingsample.adobe.AdobePushManager
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardManager
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardBinder

import com.adobe.marketing.mobile.messagingsample.ui.identity.IdentityActivity
import com.adobe.marketing.mobile.messagingsample.ui.logs.LogsActivity
import com.adobe.marketing.mobile.messagingsample.ui.logs.TimelineAdapter
import com.adobe.marketing.mobile.messagingsample.ui.places.PlacesActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var cardIdentity: LinearLayout
    private lateinit var cardPlaces: LinearLayout
    private lateinit var cardPush: LinearLayout
    private lateinit var cardInApp: LinearLayout
    private lateinit var cardJourney: LinearLayout
    private lateinit var cardLogs: LinearLayout

    // CDP Connection Card
    private lateinit var badgeCdpStatus: TextView
    private lateinit var txtDashboardEcid: TextView
    private lateinit var txtDashboardCustomerId: TextView
    private lateinit var txtDashboardPushStatus: TextView

    private lateinit var recyclerTimeline: RecyclerView
    private val timelineAdapter = TimelineAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeClicks()
        bindDashboard()
        loadCdpConnectionData()
        setupTimeline()
        askNotificationPermission()
        handleIntent(intent)
        fetchFcmToken()

        AdobeLogger.add("App", "Dashboard AEP + AJO iniciado correctamente", "INFO")
    }

    override fun onResume() {
        super.onResume()
        MobileCore.lifecycleStart(null)
        // Refresh Identity info on resume
        updateDashboardIdentity()
    }

    private fun updateDashboardIdentity() {
        AdobeIdentityManager.getECID { ecid ->
            runOnUiThread {
                if (ecid.isNotBlank()) {
                    txtDashboardEcid.text = ecid.take(20) + "..."
                    badgeCdpStatus.text = "● Conectado"
                    badgeCdpStatus.setTextColor(android.graphics.Color.parseColor("#1DB954"))
                }
            }
        }
        
        // Actualizar UI según si hay un CustomerID seteado
        val currentId = AdobeIdentityManager.currentCustomerId
        runOnUiThread {
            if (currentId.isNotBlank()) {
                txtDashboardCustomerId.text = "Known: $currentId"
                txtDashboardCustomerId.setTextColor(android.graphics.Color.parseColor("#1DB954"))
            } else {
                txtDashboardCustomerId.text = "Anonymous"
                txtDashboardCustomerId.setTextColor(android.graphics.Color.parseColor("#FA5A28"))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun initializeViews() {
        cardIdentity = findViewById(R.id.cardIdentity)
        cardPlaces = findViewById(R.id.cardPlaces)
        cardPush = findViewById(R.id.cardPush)
        cardInApp = findViewById(R.id.cardInapp)
        cardJourney = findViewById(R.id.cardJourney)
        cardLogs = findViewById(R.id.cardLogs)

        badgeCdpStatus = findViewById(R.id.badgeCdpStatus)
        txtDashboardEcid = findViewById(R.id.txtDashboardEcid)
        txtDashboardCustomerId = findViewById(R.id.txtDashboardCustomerId)
        txtDashboardPushStatus = findViewById(R.id.txtDashboardPushStatus)

        recyclerTimeline = findViewById(R.id.recyclerTimeline)
    }

    private fun initializeClicks() {
        cardIdentity.setOnClickListener {
            startActivity(Intent(this, IdentityActivity::class.java))
        }

        cardPlaces.setOnClickListener {
            startActivity(Intent(this, PlacesActivity::class.java))
        }

        cardLogs.setOnClickListener {
            startActivity(Intent(this, LogsActivity::class.java))
        }

        cardInApp.setOnClickListener {
            AdobeJourneyManager.triggerInApp("demo_event")
            Toast.makeText(this, "Trigger InApp: 'demo_event' → AJO", Toast.LENGTH_SHORT).show()
        }

        cardJourney.setOnClickListener {
            // USAMOS EL IDENTIFICADOR ACTIVO (Customer ID si existe, sino ECID)
            AdobeIdentityManager.getActiveIdentifier { activeId ->
                AdobeJourneyManager.sendInteractionEvent(
                    customerId = activeId,
                    label = "Journey Manual Trigger desde Dashboard",
                    group = "DashboardLayout",
                    category = "BCPDashboard.JourneyTrigger"
                ) { success ->
                    runOnUiThread {
                        val msg = if (success) "✓ Evento enviado como: $activeId" else "✗ Error al enviar a Edge"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        cardPush.setOnClickListener {
            val token = AdobePushManager.pushToken.value ?: ""
            if (token.isNotBlank()) {
                Toast.makeText(this, "Push Token activo: ${token.take(15)}...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Obteniendo FCM Token...", Toast.LENGTH_SHORT).show()
                fetchFcmToken()
            }
        }

        // Quick Actions
        findViewById<android.widget.Button?>(R.id.btnDashboardReset)?.setOnClickListener {
            AdobeIdentityManager.resetIdentities()
            updateDashboardIdentity()
            Toast.makeText(this, "Identidades reseteadas en CDP", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.widget.Button?>(R.id.btnDashboardSimulate)?.setOnClickListener {
            startActivity(Intent(this, PlacesActivity::class.java))
        }

        findViewById<android.widget.Button?>(R.id.btnDashboardInApp)?.setOnClickListener {
            AdobeJourneyManager.triggerInApp("demo_event")
            Toast.makeText(this, "Trigger InApp 'demo_event' enviado → AJO", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.widget.Button?>(R.id.btnDashboardPush)?.setOnClickListener {
            fetchFcmToken()
            Toast.makeText(this, "Re-registrando Push Token en Adobe", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindDashboard() {
        DashboardBinder(
            this,
            // MiniApp Views (Arriba)
            findViewById(R.id.txtIdentityStatus),
            findViewById(R.id.txtPlacesStatus),
            findViewById(R.id.txtPushStatus),
            findViewById(R.id.txtJourneyStatus),
            findViewById(R.id.txtInAppStatus),
            // Platform State Views (Abajo)
            findViewById(R.id.txtIdentityState),
            findViewById(R.id.txtPlacesState),
            findViewById(R.id.txtPushState),
            findViewById(R.id.txtJourneyState),
            findViewById(R.id.txtInAppState)
        )

        // Observar CustomerID autenticado para refrescar UI
        DashboardManager.customerAuthenticated.observe(this) { 
            updateDashboardIdentity()
        }

        // Observar Push Token
        AdobePushManager.pushToken.observe(this) { token ->
            if (token.isNotBlank()) {
                txtDashboardPushStatus.text = "Registrado ✓"
                txtDashboardPushStatus.setTextColor(android.graphics.Color.parseColor("#1DB954"))
            }
        }
    }

    private fun loadCdpConnectionData() {
        badgeCdpStatus.text = "● Conectando..."
        badgeCdpStatus.setTextColor(android.graphics.Color.parseColor("#FA5A28"))
        updateDashboardIdentity()
    }

    private fun setupTimeline() {
        recyclerTimeline.layoutManager = LinearLayoutManager(this)
        recyclerTimeline.adapter = timelineAdapter

        AdobeLogger.logsLiveData.observe(this) { logs ->
            timelineAdapter.updateLogs(logs.take(20))
        }
    }

    private fun fetchFcmToken() {
        if (!BuildConfig.FIREBASE_ENABLED) {
            AdobeLogger.add(
                "Push",
                "Firebase no configurado; registro FCM omitido",
                "INFO"
            )
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                AdobePushManager.updateToken(token)
            } else {
                AdobeLogger.add(
                    "Push",
                    "Error al obtener FCM Token: ${task.exception?.message}",
                    "ERROR"
                )
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        intent?.extras?.let {
            if (it.containsKey("messageId")) {
                Messaging.handleNotificationResponse(intent, true, null)
                AdobeLogger.add("Push", "Notificación Push clickeada/abierta desde AJO", "SUCCESS")
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
