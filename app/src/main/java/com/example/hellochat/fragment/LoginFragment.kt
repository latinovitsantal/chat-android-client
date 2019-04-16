package com.example.hellochat.fragment

import android.os.*
import android.support.v4.app.*
import android.text.method.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*

class LoginFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			verticalLayout {
				gravity = Gravity.CENTER_VERTICAL
				verticalLayout {
					editText {
						setHint(R.string.username_hint)
					}
					val passwordText = editText {
						setHint(R.string.password_hint)
						hidePassword()
					}
					checkBox(R.string.show_password) {
						onCheckedChange { _, isChecked ->
							passwordText.run {
								if (isChecked) showPassword()
								else hidePassword()
							}
						}
					}
				}.lparams(width = matchParent) {
					horizontalMargin = dip(30)
				}
			}
		}.view
	}

	fun TextView.showPassword() {
		transformationMethod = HideReturnsTransformationMethod.getInstance()
	}

	fun TextView.hidePassword() {
		transformationMethod = PasswordTransformationMethod.getInstance()
	}

}