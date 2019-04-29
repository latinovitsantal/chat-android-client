package com.example.hellochat

import android.support.v4.app.*

data class Anim(
	val forwardIn: Int,
	val forwardOut: Int,
	val backwardIn: Int,
	val backwardOut: Int
)

val Anim.reverse get() = Anim(
	backwardIn,
	backwardOut,
	forwardIn,
	forwardOut
)

fun FragmentTransaction.setCustomAnim(anim: Anim) =  setCustomAnimations(
	anim.forwardIn, anim.forwardOut, anim.backwardIn, anim.backwardOut
)

val enterFromRight = Anim(
	R.anim.enter_from_right, R.anim.exit_to_left,
	R.anim.enter_from_left, R.anim.exit_to_right
)

val enterFromTop = Anim(
	R.anim.enter_from_top, R.anim.exit_to_bottom,
	R.anim.enter_from_bottom, R.anim.exit_to_top
)

val flipOverRight = Anim(
	R.anim.flip_over_in_right, R.anim.flip_under_out_right,
	R.anim.flip_under_in_right, R.anim.flip_over_out_right
)

val flipOverLeft = Anim(
	R.anim.flip_over_in_left, R.anim.flip_under_out_left,
	R.anim.flip_under_in_left, R.anim.flip_over_out_left
)


val enterFromLeft = enterFromRight.reverse
val enterFromBottom = enterFromTop.reverse
val flipUnderRight = flipOverRight.reverse
val flipUnderLeft = flipOverLeft.reverse