package com.example.hellochat.fragment

import android.os.*
import android.support.v4.app.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import com.example.hellochat.extension.*
import com.example.hellochat.network.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*


val loginFragment by lazy { LoginFragment() }

class LoginFragment : Fragment() {

	private lateinit var progressBar: ProgressBar

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			scrollView {
				verticalLayout {
					title(R.string.login)
					val usernameText = editText("bob") {
						setHint(R.string.username_hint)
					}
					val passwordText = editText("pass") {
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
					submitButton(R.string.login) {
						onClick { login(usernameText.value, passwordText.value) }
					}
					button(R.string.register) {
						onClick { goTo(registerFragment, false, flipOverLeft) }
					}
					progressBar = progressBar {
						visibility = INVISIBLE
					}
				}.lparams(width = matchParent) {
					horizontalMargin = dip(30)
				}
			}
		}.view
	}

	private fun login(username: String, password: String) {
		progressBar.visibility = VISIBLE
		doAsync {
			val isSuccessful = ChatApi.login(username, password)
			runOnUiThread {
				progressBar.visibility = INVISIBLE
				if (isSuccessful) {
					goTo(contactsFragment, false, enterFromTop)
				}
				else snackbar(R.string.invalid_credentials)
			}
		}
	}

}