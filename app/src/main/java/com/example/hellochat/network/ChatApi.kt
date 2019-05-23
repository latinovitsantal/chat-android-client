package com.example.hellochat.network

import android.preference.*
import android.util.*
import com.example.hellochat.*
import com.example.hellochat.data.*
import okhttp3.*
import okio.*


const val CONTENT_TYPE = "Content-Type"
const val AUTHORIZATION = "Authorization"
const val FORM_URLENCODED = "application/x-www-form-urlencoded"
const val JSON = "application/json"

val formUrlEncoded = MediaType.parse(FORM_URLENCODED)!!
val jsonBody = MediaType.parse(JSON)!!


object ChatApi {

	const val COLLEGE_HOST = "http://152.66.179.232:8080"
	const val HOST = "http://169.254.134.32:8080"
	const val AUTH_USERNAME = "latantal-chat"
	const val AUTH_SECRET = "super-secret"
	const val MESSAGE_LIMIT = 20
	const val STRANGERS_LIMIT = 20
	const val ACCESS_TOKEN = "accessToken"
	const val REFRESH_TOKEN = "refreshToken"

	private val storage get() = MainActivity.instance.storage
	private val credentials = Credentials.basic(AUTH_USERNAME, AUTH_SECRET)
	private var getAccessToken: () -> String = { storage.accessToken }

	private fun refreshTokenPreferences() {
		PreferenceManager.getDefaultSharedPreferences(MainActivity.instance)
			.edit()
			.putString(ChatApi.ACCESS_TOKEN, storage.accessToken)
			.putString(ChatApi.REFRESH_TOKEN, storage.refreshToken)
			.apply()
	}

	private val client = OkHttpClient.Builder().addInterceptor {
		it.proceed(it.request().newBuilder().invoke {
			header(CONTENT_TYPE, JSON)
			val accessToken = getAccessToken()
			if (accessToken.isNotEmpty())
				header(AUTHORIZATION, "Bearer $accessToken")
		})
	}.build()


	fun <R> runWithAccessToken(accessToken: String, run: ChatApi.() -> R): R {
		val prevGetter = getAccessToken
		getAccessToken = { accessToken }
		val result = this.run()
		getAccessToken = prevGetter
		return result
	}

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
					refreshTokenPreferences()
					this.username = username
				}
				isSuccessful = true
			}
		}
		return isSuccessful
	}

	fun refreshToken(): Boolean {
		storage.accessToken = ""
		var success = false
		executeRequest {
			url("$HOST/oauth/token")
			header(CONTENT_TYPE, FORM_URLENCODED)
			header(AUTHORIZATION, credentials)
			post(formUrlEncoded, "refresh_token=${storage.refreshToken}&grant_type=refresh_token")
		}!!.consume { body ->
			if (this.isSuccessful) {
				success = true
				val json = body.readJson<Map<String, String>>()
				storage.run {
					accessToken = json.getValue("access_token")
					refreshToken = json.getValue("refresh_token")
					refreshTokenPreferences()
				}
			}
		}
		return success
	}

	fun getContacts(saveAlso: Boolean = true): List<Contact> {
		var contacts = listOf<Contact>()
		executeRequest { url("$HOST/contacts") }!!.consume { body ->
			if (isSuccessful) {
				contacts = body.readJson()
				if (saveAlso) {
					contacts.forEach { contact ->
						storage.contacts.find { it.id == contact.id }
							?.refreshFrom(contact)
							?: storage.contacts.add(contact.apply { messages = mutableListOf() })
					}
				}
			}
		}
		return contacts
	}

	fun getRequests(): List<FriendRequest> {
		var requests = listOf<FriendRequest>()
		executeRequest { url("$HOST/contacts/requests") }!!.consume { body ->
			if (isSuccessful) {
				requests = body.readJson()
				storage.requests.run {
					clear()
					addAll(requests)
				}
			}
		}
		return requests
	}

	fun getMessagesOf(contact: Contact): MutableList<Message> {
		var messages = mutableListOf<Message>()
		executeRequest { url("$HOST/messages?id=${contact.id}&limit=$MESSAGE_LIMIT") }!!.consume { body ->
			if (isSuccessful) {
				messages = body.readJson()
				contact.messages.run {
					clear()
					addAll(messages)
				}
			}
		}
		return messages
	}

	fun getPrevMessagesOf(contact: Contact): MutableList<Message> {
		var prevMessages = mutableListOf<Message>()
		executeRequest {
			url("$HOST/messages?id=${contact.id}&limit=$MESSAGE_LIMIT&before=${contact.messages.last().id}")
		}!!.consume { body ->
			if (isSuccessful) {
				prevMessages = body.readJson()
				contact.messages.addAll(prevMessages)
			}
		}
		return prevMessages
	}

	fun seeMessagesOf(contact: Contact): MutableList<Message> {
		var newMessages = mutableListOf<Message>()
		executeRequest {
			url("$HOST/messages/see?convoId=${contact.id}&lastSeenId=${contact.messages.first().id}")
			post(jsonBody, "")
		}!!.consume { body ->
			if (isSuccessful) {
				newMessages = body.readJson()
				newMessages.reverse()
				contact.messages.addAll(0, newMessages)
			}
		}
		return newMessages
	}

	fun sendMessage(text: String, contact: Contact): Boolean {
		var success = false
		executeRequest {
			url("$HOST/messages")
			post(jsonBody, PostMessage(contact.id, text).jsonString)
		}!!.consume {
			if (isSuccessful)
				success = true
		}
		return success
	}

	fun declineRequest(friendRequest: FriendRequest): Boolean {
		var success = false
		executeRequest {
			url("$HOST/contacts/requests/rejections?sender=${friendRequest.username}")
			post(jsonBody, "")
		}!!.consume {
			if (isSuccessful)
				success = true
		}
		return true
	}

	fun acceptRequest(friendRequest: FriendRequest): Boolean {
		var success = false
		executeRequest {
			url("$HOST/contacts/requests/acceptances?sender=${friendRequest.username}")
			post(jsonBody, "")
		}!!.consume {
			if (isSuccessful)
				success = true
		}
		return true
	}

	fun getStrangersWithNameContains(searchTerm: String): List<String> {
		var result = listOf<String>()
		executeRequest { url("$HOST/users?searchTerm=$searchTerm&limit=$STRANGERS_LIMIT") }!!.consume {
			if (isSuccessful)
				result = it.readJson<List<String>>().filter { name -> name != storage.username }
		}
		return result
	}

	fun sendRequest(receiver: String): Boolean {
		var success = false
		executeRequest {
			url("$HOST/contacts/requests?receiver=$receiver")
			put(jsonBody, "")
		}
		return success
	}

}