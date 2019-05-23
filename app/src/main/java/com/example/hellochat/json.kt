package com.example.hellochat

import android.util.*
import com.google.gson.*
import com.google.gson.reflect.*
import java.io.*

val gson = Gson()

inline fun <reified T> String.readJson(): T = gson.fromJson(this, object: TypeToken<T>(){}.type)
val Any.jsonString: String get() = gson.toJson(this)

inline fun <reified T> File.readJson(): T = readText().also { Log.i("json read ${this.name}", it) }.readJson()
fun File.writeJson(any: Any) = writeText(any.jsonString.also { Log.i("json write ${this.name}", it)})
