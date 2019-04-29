package com.example.hellochat.fragment

import android.os.*
import android.support.v4.app.*
import android.view.*
import com.example.hellochat.*
import com.example.hellochat.extension.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*


val registerFragment by lazy { RegisterFragment() }

class RegisterFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			scrollView {
				verticalLayout {
					title(R.string.register)
					val usernameText = editText { setHint(R.string.username_hint) }
					val passwordText = editText {
						setHint(R.string.password_hint)
						hidePassword()
					}
					val repeatPasswordText = editText {
						setHint(R.string.repeat_password_hint)
						hidePassword()
					}
					checkBox(R.string.show_password) {
						onCheckedChange { _, isChecked ->
							if (isChecked) {
								passwordText.showPassword()
								repeatPasswordText.showPassword()
							}
							else {
								passwordText.showPassword()
								repeatPasswordText.hidePassword()
							}
						}
					}
					submitButton(R.string.register) {
						onClick { register(usernameText.value, passwordText.value, repeatPasswordText.value) }
					}
					button(R.string.login) {
						onClick { goTo(loginFragment, false, flipUnderLeft) }
					}
				}.lparams(width = matchParent) { horizontalMargin = dip(30) }
			}
		}.view
	}

	private fun register(username: String, password: String, repeatPassword: String) {
		errorChain(
			username.isEmpty() to R.string.must_enter_username,
			(password != repeatPassword) to R.string.password_not_same
		) ?: return
		toast("$username $password")
	}

}