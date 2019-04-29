package com.example.hellochat.network

import okhttp3.*

data class ConsumableBody(val response: Response, val body: String) {
	fun consume(consumer: Response.(String) -> Unit) = response.consumer(body)
}