package com.example.hellochat.extension

import android.support.v4.app.*
import com.example.hellochat.*
import com.example.hellochat.fragment.*
import org.jetbrains.anko.design.*

val loginFragment by lazy { LoginFragment() }
val registerFragment by lazy { RegisterFragment() }

val Fragment.mainActivity get() = activity as MainActivity
fun Fragment.goTo(fragment: Fragment, animOut: Int, animIn: Int) = mainActivity.goTo(fragment, animOut, animIn)
fun Fragment.snackbar(message: Int) = view!!.snackbar(message)

fun Fragment.errorChain(vararg conditionMessage: Pair<Boolean, Int>): Unit? {
	for ((cond, msg) in conditionMessage) {
		if (cond) {
			snackbar(msg)
			return null
		}
	}
	return Unit
}