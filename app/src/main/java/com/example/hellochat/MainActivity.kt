package com.example.hellochat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.*
import com.example.hellochat.extension.*
import com.example.hellochat.fragment.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		//findViewById<View>(R.id.fragment_view) ?: return
		//savedInstanceState ?: return

		fragmentTransact {
			add(R.id.fragment_container , loginFragment)
		}

	}

	fun goTo(fragment: Fragment, animOut: Int, animIn: Int) {
		fragmentTransact {
			setCustomAnimations(animIn, animOut)
			replace(R.id.fragment_container, fragment)
			addToBackStack(null)
		}
	}

	private fun fragmentTransact(run: FragmentTransaction.() -> Unit) {
		supportFragmentManager.beginTransaction().apply(run).commit()
	}

}
