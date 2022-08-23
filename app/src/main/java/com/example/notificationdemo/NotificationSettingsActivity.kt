package com.example.notificationdemo

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotificationSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        cancelNotificationIfExists()
    }

    private fun cancelNotificationIfExists() {
        val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID_KEY, 0)
        val notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.cancel(notificationId)
    }
}