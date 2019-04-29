package com.example.hellochat.data

import android.content.*
import com.example.hellochat.*
import java.io.*

data class Storage(
	var accessToken: String = "",
	var refreshToken: String = "",
	var username: String = "",
	var conversations: MutableMap<Contact, MutableList<Message>> = mutableMapOf()
) {

	fun save(context: Context) {
		File(context.filesDir, FILENAME).writeJson(this)
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





