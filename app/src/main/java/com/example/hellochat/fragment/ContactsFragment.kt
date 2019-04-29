package com.example.hellochat.fragment

import android.graphics.*
import android.os.*
import android.support.v4.app.*
import android.util.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import com.example.hellochat.data.*
import com.example.hellochat.extension.*
import com.example.hellochat.network.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*
import java.lang.Exception


val contactsFragment by lazy { ContactsFragment() }

class ContactsFragment : Fragment() {

	lateinit var scrollView: ScrollView
	lateinit var contactsView: LinearLayout
	lateinit var progressBar: ProgressBar

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			frameLayout {
				verticalLayout {
					editText { setHint(R.string.search_for_contacts) }
					progressBar = progressBar {
						visibility = GONE
					}
					scrollView = scrollView().lparams(height = matchParent, width = matchParent)
				}.lparams(height = matchParent, width = matchParent)
			}
		}.view
	}

	override fun onStart() {
		super.onStart()
		progressBar.visibility = VISIBLE
		doAsync {
			ChatApi.getContacts()
			runOnUiThread {
				progressBar.visibility = GONE
				updateContacts()
			}
		}
	}

	private fun updateContacts() {
		contactsView = UI {
			linearLayout {
				storage.conversations.keys.forEach { viewOf(it) }
			}
		}.view as LinearLayout
		scrollView.run {
			removeAllViews()
			addView(contactsView)
		}
	}

	private fun _LinearLayout.viewOf(contact: Contact) =
		linearLayout {
			linearLayout {
				imageView(R.drawable.ic_round_person_24px).lparams(height = matchParent)
				textView("${contact.username} (${contact.unseenCount})") {
					gravity = Gravity.CENTER
					textSize = 22f
					textColor = Color.BLACK
				}.lparams { margin = dip(5) }
			}.lparams { weight = 1f }
			button(R.string.send_message) {
				onClick {
					try {
						goTo(conversationFragment(contact), true, enterFromRight)
					} catch (e:Exception) {
						Log.i("chatfragment", e.message)
					}
				}
			}
		}.lparams(width = matchParent) { margin = dip(5) }

}