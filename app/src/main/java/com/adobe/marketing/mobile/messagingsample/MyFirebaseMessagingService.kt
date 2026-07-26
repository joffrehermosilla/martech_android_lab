package com.adobe.marketing.mobile.messagingsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import com.adobe.marketing.mobile.messaging.MessagingService
import com.adobe.marketing.mobile.messagingsample.adobe.AdobePushManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio Enterprise para captura de Push AJO.
 * Incluye soporte para canales específicos y sonidos personalizados.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AdobePushManager.updateToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val data = message.data
        AdobePushManager.handlePayload(data)

        // Configuración de canal con sonido si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = data["adb_channel_id"] ?: "ajo_default_channel"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Mensajes Corporativos",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Canal para notificaciones AJO con sonido"
                    enableLights(true)
                    // Aquí se podría setear un sonido específico desde raw si viene en el payload
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Delegar al SDK de Adobe para mostrar la notificación
        MessagingService.handleRemoteMessage(this, message)
    }
}
