package com.example.hellochat

import android.os.*
import android.support.v4.app.*
import android.support.v7.app.*
import com.example.hellochat.data.*
import com.example.hellochat.extension.*
import com.example.hellochat.fragment.*

class MainActivity : AppCompatActivity() {

	lateinit var storage: Storage

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		instance = this

		fragmentTransact {
			add(R.id.fragment_container , loginFragment)
		}

		storage = Storage.load(this)

	}

	fun goTo(fragment: Fragment, addToBackStackNeeded: Boolean, anim: Anim?) {
		fragmentTransact {
			if (anim != null) setCustomAnim(anim)
			replace(R.id.fragment_container, fragment)
			if (addToBackStackNeeded) addToBackStack(null)
		}
	}

	private fun fragmentTransact(run: FragmentTransaction.() -> Unit) {
		supportFragmentManager.beginTransaction().apply(run).commit()
	}

	companion object {
		lateinit var instance: MainActivity
			private set
	}

}
