package com.example.hellochat

import com.google.gson.*
import com.google.gson.reflect.*
import java.io.*

val gson = Gson()

inline fun <reified T> String.readJson(): T = gson.fromJson(this, object: TypeToken<T>(){}.type)
val Any.jsonString: String get() = gson.toJson(this)

inline fun <reified T> File.readJson(): T = readText().readJson()
fun File.writeJson(any: Any) = writeText(any.jsonString)
