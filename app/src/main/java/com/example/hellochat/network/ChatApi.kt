package com.example.hellochat.network

import android.util.*
import com.example.hellochat.*
import com.example.hellochat.data.*
import okhttp3.*
import okio.Buffer


const val OK = 200

const val CONTENT_TYPE = "Content-Type"
const val AUTHORIZATION = "Authorization"
const val FORM_URLENCODED = "application/x-www-form-urlencoded"
const val JSON = "application/json"

val formUrlEncoded = MediaType.parse(FORM_URLENCODED)!!



object ChatApi {

	const val HOST = "http://152.66.179.232:8080"
	const val AUTH_USERNAME = "latantal-chat"
	const val AUTH_SECRET = "super-secret"
	const val MESSAGE_LIMIT = 20

	private val storage get() = MainActivity.instance.storage
	private val credentials = Credentials.basic(AUTH_USERNAME, AUTH_SECRET)
	private val client = OkHttpClient.Builder().addInterceptor {
		it.proceed(it.request().newBuilder().invoke {
			header(CONTENT_TYPE, JSON)
			if (storage.accessToken.isNotEmpty())
				header(AUTHORIZATION, "Bearer " + storage.accessToken)
		})
	}.build()

	private fun executeRequest(builder: Request.Builder.() -> Unit): ConsumableBody? {
		return try {
			val request = Request.Builder().apply(builder).build()
			Log.i("chatapi request url", request.method().toString() + " " + request.url().toString())
			Buffer().also {
				request.body()?.writeTo(it)
				Log.i("chatapi request body", it.readUtf8())
			}
			val response = client.newCall(request).execute()
			ConsumableBody(response, response.body()!!.string()).also { Log.i("chatapi response", it.body) }
		} catch (e: Throwable) {
			Log.i("chatapi exception", e.message)
			null
		}
	}

	fun login(username: String, password: String): Boolean {
		var isSuccessful = false
		executeRequest {
			url("$HOST/oauth/token")
			header(CONTENT_TYPE, FORM_URLENCODED)
			header(AUTHORIZATION, credentials)
			post(formUrlEncoded, "username=$username&password=$password&grant_type=password")
		}?.consume { body ->
			if (this.isSuccessful) {
				val json = body.readJson<Map<String, String>>()
				storage.run {
					accessToken = json.getValue("access_token")
					refreshToken = json.getValue("refresh_token")
					this.username = username
				}
				isSuccessful = true
			}
		}
		return isSuccessful
	}

	fun getContacts(): List<Contact>? {
		var contacts = listOf<Contact>()
		executeRequest { url("$HOST/contacts") }!!.consume { body ->
			if (isSuccessful) {
				contacts = body.readJson()
				contacts.forEach { contact ->
					storage.conversations.run {
						keys.find { it == contact }
							?.run { refreshFrom(contact) }
							?: put(contact, mutableListOf())
					}
				}
			}
		}
		return contacts
	}

	fun getMessagesOf(contact: Contact): MutableList<Message> {
		var messages = mutableListOf<Message>()
		executeRequest { url("$HOST/messages?id=${contact.id}&limit=$MESSAGE_LIMIT") }!!.consume { body ->
			if (isSuccessful) {
				messages = body.readJson()
				storage.conversations[contact]!!.run {
					clear()
					addAll(messages)
				}
			}
		}
		return messages
	}

	fun getPrevMessagesOf(contact: Contact): MutableList<Message> {
		val contactMessages = storage.conversations[contact]!!
		var prevMessages = mutableListOf<Message>()
		executeRequest {
			url("$HOST/messages?id=${contact.id}&limit=$MESSAGE_LIMIT&before=${contactMessages.last().id}")
		}!!.consume { body ->
			if (isSuccessful) {
				prevMessages = body.readJson()
				contactMessages.addAll(prevMessages)
			}
		}
		return prevMessages
	}

}