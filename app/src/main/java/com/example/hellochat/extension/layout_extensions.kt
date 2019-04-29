package com.example.hellochat.extension

import android.support.v4.content.*
import android.text.method.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import org.jetbrains.anko.*

fun TextView.showPassword() {
	transformationMethod = HideReturnsTransformationMethod.getInstance()
}

fun TextView.hidePassword() {
	transformationMethod = PasswordTransformationMethod.getInstance()
}

val TextView.value get() = text.toString()

fun _LinearLayout.title(title: Int, builder: TextView.() -> Unit = {}) =
	textView(title) {
		textSize = sp(18).toFloat()
		textAlignment = TextView.TEXT_ALIGNMENT_CENTER
	}.lparams(width = matchParent) {
		verticalMargin = dip(30)
	}.apply(builder)

fun ViewManager.submitButton(text: Int, builder: Button.() -> Unit = {}) =
	button(text) {
		backgroundColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
		textColor = ContextCompat.getColor(context, R.color.colorWhite)
		builder()
	}

val GONE = View.GONE
val VISIBLE = View.VISIBLE
val INVISIBLE = View.INVISIBLE