package com.example.hellochat.data

import android.content.*
import com.example.hellochat.*
import java.io.*

data class Storage(
	var accessToken: String = "",
	var refreshToken: String = "",
	var username: String = "",
	var contacts: MutableList<Contact> = mutableListOf(),
	var requests: MutableList<FriendRequest> = mutableListOf()
) {

	fun save(context: Context) {
		File(context.filesDir, FILENAME).writeJson(this)
	}

	fun logout() {
		accessToken = ""
		refreshToken = ""
		username = ""
		contacts = mutableListOf()
	}

	companion object {

		const val FILENAME = "main.json"

		fun load(context: Context): Storage {
			return File(context.filesDir, FILENAME).run {
				when {
					!exists() -> Storage().also { createNewFile(); writeJson(it) }
					isEmpty() -> Storage().also { writeJson(it) }
					else -> readJson()
				}
			}
		}

		fun File.isEmpty() = readText().isEmpty()

	}

}





