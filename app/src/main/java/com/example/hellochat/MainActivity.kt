package com.example.hellochat

import android.os.*
import android.preference.*
import android.support.v4.app.*
import android.support.v7.app.*
import android.util.*
import com.example.hellochat.data.*
import com.example.hellochat.extension.*
import com.example.hellochat.fragment.*
import org.jetbrains.anko.*
import java.io.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

	lateinit var storage: Storage

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		instance = this
	}

	override fun onStart() {
		super.onStart()
		setIsActive(true)
		doAsync {
			File(filesDir, "main.json").delete() //Only for tests!
			storage = Storage.load(instance)
			val fragment = when {
				storage.accessToken.isNotEmpty() -> stayLoggedInFragment
				else -> loginFragment
			}
			runOnUiThread {
				fragmentTransact { add(R.id.fragment_container, fragment) }
			}
		}
	}

	override fun onPause() {
		super.onPause()
		setIsActive(false)
		doAsync {
			storage.save(instance)
		}
	}

	override fun onStop() {
		super.onStop()
		setIsActive(false)
		doAsync {
			storage.save(instance)
		}
	}

	private fun setIsActive(isActive: Boolean) {
		PreferenceManager.getDefaultSharedPreferences(this)
			.edit()
			.putBoolean(IS_ACTIVE, isActive)
			.apply()
	}

	fun goTo(fragment: Fragment, addToBackStackNeeded: Boolean, anim: Anim?) {
		fragmentTransact {
			if (anim != null) setCustomAnim(anim)
			replace(R.id.fragment_container, fragment)
			if (addToBackStackNeeded) addToBackStack(null)
		}
	}

	fun popAllFragments() {
		supportFragmentManager.run {
			repeat(backStackEntryCount) {
				popBackStack()
			}
		}
	}

	private fun fragmentTransact(run: FragmentTransaction.() -> Unit) {
		supportFragmentManager.beginTransaction().apply(run).commit()
	}

	companion object {
		lateinit var instance: MainActivity
			private set
		const val IS_ACTIVE = "isActive"
	}

}
