package com.example.hellochat

import android.app.*
import android.content.*
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.os.*
import android.util.*

class HelloChatBroadcastReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == ACTION_BOOT_COMPLETED)
			HelloChatService.scheduleJob(context)
		Log.i("chatbackground", "broadcast received")
		createNotificationChannel(context)
	}

	private fun createNotificationChannel(context: Context) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = context.getString(R.string.channel_name)
			val descriptionText = context.getString(R.string.channel_description)
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel(HelloChatService.NOTIFICATION_CHANNEL_ID, name, importance).apply {
				description = descriptionText
			}
			// Register the channel with the system
			val notificationManager: NotificationManager =
				context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)
		}
	}

}

// Trigger ACTION_BOOT_COMPLETED:
// C:\Users\Anti\AppData\Local\Android\Sdk\platform-tools>adb root
// C:\Users\Anti\AppData\Local\Android\Sdk\platform-tools>adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -p com.example.hellochat
