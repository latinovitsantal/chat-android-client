package com.example.hellochat.fragment

import android.support.v4.app.*
import com.example.hellochat.*
import org.jetbrains.anko.design.*


val Fragment.mainActivity get() = activity as MainActivity

fun Fragment.goTo(fragment: Fragment, addToBackStackNeeded: Boolean = false, anim: Anim? = null)
		= mainActivity.goTo(fragment, addToBackStackNeeded, anim)

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

val Fragment.storage get() = mainActivity.storage