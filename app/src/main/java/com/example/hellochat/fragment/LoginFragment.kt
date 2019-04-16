package com.example.hellochat.fragment

import android.graphics.*
import android.opengl.*
import android.os.*
import android.support.v4.app.*
import android.support.v4.content.*
import android.text.method.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import com.example.hellochat.extension.*
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*

class LoginFragment : Fragment() {

	lateinit var progressBar: ProgressBar

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			scrollView {
				verticalLayout {
					title(R.string.login)
					val usernameText = editText {
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
					submitButton(R.string.login) {
						onClick { login(usernameText.value, passwordText.value) }
					}
					button(R.string.register) {
						onClick { goTo(registerFragment, flipOutRight, flipInRight) }
					}
					progressBar = progressBar {
						visibility = View.INVISIBLE
					}
				}.lparams(width = matchParent) {
					horizontalMargin = dip(30)
				}
			}
		}.view
	}

	suspend fun login(username: String, password: String) {
		progressBar.visibility = View.VISIBLE
		toast("$username $password")
		delay(1000)
		progressBar.visibility = View.INVISIBLE
	}

}