package com.adobe.marketing.mobile.messagingsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.adobe.marketing.mobile.messaging.MessagingService
import com.adobe.marketing.mobile.messagingsample.adobe.AdobePushManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio Enterprise para captura de Push AJO.
 * Alineado con los estándares del banco: canal PushBCP001 y sonido sonomarca.
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ALINEACIÓN: Usamos el canal PushBCP001 por defecto si no viene en el payload
            val channelId = data["adb_channel_id"] ?: "PushBCP001"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Notificaciones BCP",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Canal corporativo para campañas AJO"
                    enableLights(true)
                    enableVibration(true)
                    
                    // Configuración de sonido: sonomarca
                    val soundUri = Uri.parse("android.resource://" + packageName + "/raw/sonomarca")
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                    setSound(soundUri, audioAttributes)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Delegar al SDK de Adobe para el tracking de entrega y visualización
        MessagingService.handleRemoteMessage(this, message)
    }
}
