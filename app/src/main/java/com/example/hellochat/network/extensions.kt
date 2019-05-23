package com.example.hellochat.network

import okhttp3.*

fun Request.Builder.post(contentType: MediaType, body: String) {
	post(RequestBody.create(contentType, body))
}

fun Request.Builder.put(contentType: MediaType, body: String) {
	put(RequestBody.create(contentType, body))
}

operator fun Request.Builder.invoke(builder: Request.Builder.() -> Unit): Request {
	builder()
	return build()
}