package com.example.hellochat

import android.app.*
import android.app.TaskStackBuilder
import android.app.job.*
import android.content.*
import android.preference.*
import android.support.v4.app.*
import android.util.*
import com.example.hellochat.network.*
import org.jetbrains.anko.*


class HelloChatService : JobService() {

	private val isMainActivityActive get() =
		PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MainActivity.IS_ACTIVE, false)

	private val accessToken get() =
		PreferenceManager.getDefaultSharedPreferences(this).getString(ChatApi.ACCESS_TOKEN, "")!!

	private fun createNotification(unseenContactCount: Int) {
		val intent = Intent(this, MainActivity::class.java)
		val pendingIntent = TaskStackBuilder.create(this).run {
			addNextIntentWithParentStack(intent)
			getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
		}
		val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_round_chat_24px)
			.setContentTitle(getString(R.string.unsee_messages))
			.setContentText("You have unseen messages from $unseenContactCount contacts.")
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
			.build()
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(1, notification)
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		val context = this
		Log.i("chatbackground", "job started")
		doAsync {
			val accToken = accessToken
			Log.i("chatbackground", "trying with $accToken")
			if (!isMainActivityActive && accToken.isNotEmpty()) {
				val contacts = ChatApi.runWithAccessToken(accToken) {
					getContacts(false)
				}
				val unseenContactCount = contacts.map { it.unseenCount }.sum()
				if (unseenContactCount > 0)
					createNotification(unseenContactCount)
				else createNotification(0)
			} else {
				Log.i("chatbackground", "didn't fetch")
			}
			jobFinished(params, true)
			scheduleJob(context)
		}
		return true
	}

	override fun onStopJob(params: JobParameters?): Boolean {
		return true
	}

	companion object {
		const val NOTIFICATION_CHANNEL_ID = "hellochat notification channel"
		fun scheduleJob(context: Context) {
			val serviceComponent = ComponentName(context, HelloChatService::class.java)
			val jobInfo = JobInfo.Builder(0, serviceComponent).apply {
				setMinimumLatency(2000)
			}.build()
			context.getSystemService(JobScheduler::class.java)
				.schedule(jobInfo)
		}

	}

}
