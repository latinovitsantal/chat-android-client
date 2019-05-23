package com.example.hellochat.fragment

import android.os.*
import android.support.v4.app.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import org.jetbrains.anko.sdk25.coroutines.*

val accountFragment by lazy { AccountFragment() }

class AccountFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_account, container, false).apply {
			findViewById<Button>(R.id.logoutBtn).onClick {
				storage.logout()
				mainActivity.popAllFragments()
				goTo(loginFragment, false, enterFromBottom)
			}
			findViewById<TextView>(R.id.nameTextView).text = storage.username
		}
	}

}