package com.arcadesoftware.lykonbrowser.browser.engine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.arcadesoftware.lykonbrowser.MainActivity

class TorNotificationService : Service() {
    companion object {
        const val CHANNEL_ID = "TorModeChannel"
        const val ACTION_CLOSE_TOR = "com.arcadesoftware.lykonbrowser.CLOSE_TOR"
        const val NOTIFICATION_ID = 1002
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_CLOSE_TOR) {
            val closeIntent = Intent(this, MainActivity::class.java).apply {
                action = ACTION_CLOSE_TOR
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(closeIntent)
            stopSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val closePendingIntent = PendingIntent.getService(
            this, 0,
            Intent(this, TorNotificationService::class.java).apply { action = ACTION_CLOSE_TOR },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tor Connection Active")
            .setContentText("Tap to disconnect and close Tor tabs")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentIntent(closePendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Tor Browsing",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
