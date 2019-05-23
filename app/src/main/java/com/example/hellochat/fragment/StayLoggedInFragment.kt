package com.example.hellochat.fragment

import android.os.*
import android.support.v4.app.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import com.example.hellochat.network.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*

val stayLoggedInFragment by lazy { StayLoggedInFragment() }

class StayLoggedInFragment : Fragment() {

	private lateinit var progressBar: ProgressBar

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			verticalLayout {
				gravity = Gravity.CENTER
				progressBar = progressBar()
			}
		}.view
	}

	override fun onStart() {
		super.onStart()
		doAsync {
			val fragment = when {
				ChatApi.refreshToken() -> contactsFragment
				else -> loginFragment
			}
			runOnUiThread { goTo(fragment, false, enterFromTop) }
		}
	}

}