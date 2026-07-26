package com.adobe.marketing.mobile.messagingsample.ui.dashboard

import android.graphics.Color
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner

/**
 * Vincula los estados del DashboardManager con los componentes visuales.
 * Actualiza tanto los MiniApp Status como el Platform Status central.
 */
class DashboardBinder(
    owner: LifecycleOwner,
    // MiniApp Status Views (Arriba)
    private val txtIdentity: TextView?,
    private val txtPlaces: TextView?,
    private val txtPush: TextView?,
    private val txtJourney: TextView?,
    private val txtInApp: TextView?,
    // Platform Status Views (Abajo - item_status.xml)
    private val txtIdentityState: TextView?,
    private val txtPlacesState: TextView?,
    private val txtPushState: TextView?,
    private val txtJourneyState: TextView?,
    private val txtInAppState: TextView?
) {
    init {
        val successColor = Color.parseColor("#1DB954")
        val warningColor = Color.parseColor("#FA5A28")

        DashboardManager.sdkConnected.observe(owner) { isConnected ->
            val text = if (isConnected) "SDK Activo" else "No Conectado"
            txtIdentity?.text = text
            txtIdentityState?.text = if (isConnected) "Connected" else "Disconnected"
            txtIdentityState?.setTextColor(if (isConnected) successColor else Color.RED)
        }

        DashboardManager.placesConnected.observe(owner) { isConnected ->
            txtPlaces?.text = if (isConnected) "Places Activo" else "Simulación"
            txtPlacesState?.text = if (isConnected) "Ready" else "Waiting"
            txtPlacesState?.setTextColor(if (isConnected) successColor else warningColor)
        }

        DashboardManager.pushConnected.observe(owner) { isConnected ->
            txtPush?.text = if (isConnected) "Token Registrado" else "Sin Token"
            txtPushState?.text = if (isConnected) "Ready" else "Disconnected"
            txtPushState?.setTextColor(if (isConnected) successColor else Color.RED)
        }

        DashboardManager.edgeConnected.observe(owner) { isConnected ->
            txtJourney?.text = if (isConnected) "Edge Conectado" else "Listo"
            txtJourneyState?.text = if (isConnected) "Ready" else "Waiting"
            txtJourneyState?.setTextColor(if (isConnected) successColor else warningColor)
        }

        DashboardManager.customerAuthenticated.observe(owner) { isAuthenticated ->
            if (isAuthenticated) {
                txtIdentity?.text = "Known Customer"
                txtIdentityState?.text = "Authenticated"
                txtIdentityState?.setTextColor(successColor)
            }
        }
        
        // InApp Status sync
        txtInApp?.text = "Ready"
        txtInAppState?.text = "Ready"
        txtInAppState?.setTextColor(successColor)
    }
}
