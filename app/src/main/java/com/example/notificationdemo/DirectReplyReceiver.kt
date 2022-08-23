package com.example.notificationdemo

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class DirectReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        receiveInput(context, intent)
    }

    private fun receiveInput(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null) {
            val inputString = remoteInput.getCharSequence(Constants.KEY_REPLY).toString()

            val repliedNotification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_alert)
                .setContentText("Your reply is received: $inputString")
                .build()
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Constants.NOTIFICATION_ID, repliedNotification)
        }
    }
}