package com.example.notificationdemo

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.example.notificationdemo.Constants.CHANNEL_ID
import com.example.notificationdemo.Constants.NOTIFICATION_ID
import com.example.notificationdemo.Constants.NOTIFICATION_ID_KEY
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    displayNotification()
                } else {
                    Snackbar.make(
                        findViewById<View>(android.R.id.content).rootView,
                        getString(R.string.grant_notification_permission_msg),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        findViewById<Button>(R.id.btnGetNotification).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                displayNotification()
            } else {
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun displayNotification() {
        createNotificationChannel()

        val intent = Intent(this@MainActivity, SecondActivity::class.java)
        val pendingIntent = getActivity(
            this,
            0,
            intent,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )

        val action1PendingIntent = getAction1PendingIntent()
        val action2PendingIntent = getAction2PendingIntent()
        val action3PendingIntent = getAction3PendingIntent()

        val notification =
            getNotification(
                pendingIntent,
                action1PendingIntent,
                action2PendingIntent,
                action3PendingIntent
            )

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getAction1PendingIntent(): PendingIntent? {
        val notificationDetailsIntent =
            Intent(this@MainActivity, NotificationDetailActivity::class.java)
        notificationDetailsIntent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)

        return getActivity(
            this,
            0,
            notificationDetailsIntent,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )
    }

    private fun getAction2PendingIntent(): PendingIntent? {
        val notificationSettingsIntent =
            Intent(this@MainActivity, NotificationSettingsActivity::class.java)
        notificationSettingsIntent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)

        return getActivity(
            this,
            0,
            notificationSettingsIntent,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )
    }

    private fun getAction3PendingIntent(): PendingIntent? {
        val replyIntent =
            Intent(this, DirectReplyReceiver::class.java)
        replyIntent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)

        return getBroadcast(
            this,
            0,
            replyIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FLAG_MUTABLE or FLAG_UPDATE_CURRENT
            } else {
                FLAG_UPDATE_CURRENT
            }
        )
    }

    private fun getNotification(
        pendingIntent: PendingIntent?,
        action1PendingIntent: PendingIntent?,
        action2PendingIntent: PendingIntent?,
        action3PendingIntent: PendingIntent?
    ): Notification {


        val remoteInput: RemoteInput = RemoteInput.Builder(Constants.KEY_REPLY).run {
            setLabel("Message")
            build()
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_notification_alert)
            .setContentText(getString(R.string.notification_content))
            .setAutoCancel(true)
            .setContentIntent(
                pendingIntent
            )
            .addAction(
                NotificationCompat.Action(
                    null,
                    getString(R.string.details),
                    action1PendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    null,
                    getString(R.string.settings),
                    action2PendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    null,
                    getString(R.string.reply),
                    action3PendingIntent
                )
                    .addRemoteInput(remoteInput)
                    .build()
            )
            .build()
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }
}