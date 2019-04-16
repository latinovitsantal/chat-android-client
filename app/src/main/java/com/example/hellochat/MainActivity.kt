package com.example.hellochat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hellochat.fragment.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		//findViewById<View>(R.id.fragment_view) ?: return
		//savedInstanceState ?: return

		supportFragmentManager.beginTransaction()
			.add(R.id.fragment_view , loginFragment).commit()

	}
}
