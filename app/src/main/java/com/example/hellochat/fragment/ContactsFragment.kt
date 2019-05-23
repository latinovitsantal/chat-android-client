package com.example.hellochat.fragment

import android.graphics.*
import android.os.*
import android.support.v4.app.*
import android.text.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import com.example.hellochat.data.*
import com.example.hellochat.extension.*
import com.example.hellochat.network.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*


val contactsFragment by lazy { ContactsFragment() }

class ContactsFragment : Fragment() {

	private lateinit var contactsView: LinearLayout
	private lateinit var progressBar: ProgressBar
	private var searchTerm = ""

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			frameLayout {
				verticalLayout {
					linearLayout {
						textView(R.string.app_name) {
							textSize = 30f
						}
						textView(storage.username) {
							gravity = Gravity.END
							textSize = 20f
							onClick { goTo(accountFragment, true, flipOverLeft) }
						}.lparams(weight = 1f) { rightMargin = dip(10) }
					}
					editText {
						setHint(R.string.search_for_contacts)
						addTextChangedListener(object : TextWatcher {
							override fun afterTextChanged(s: Editable?) {
								searchTerm = s.toString()
								when {
									s!!.isEmpty() -> fetchAndRefresh()
									else -> searchAndRefresh()
								}
							}
							override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
							override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
						})
					}
					progressBar = progressBar { visibility = GONE }
					scrollView {
						contactsView = verticalLayout().lparams(width = matchParent)
					}.lparams(height = matchParent, width = matchParent)
				}.lparams(height = matchParent, width = matchParent)
			}
		}.view
	}

	override fun onResume() {
		super.onResume()
		fetchAndRefresh()
	}

	private fun searchAndRefresh() {
		doAsync {
			val strangers = ChatApi.getStrangersWithNameContains(searchTerm)
			runOnUiThread {
				contactsView.run {
					removeAllViews()
					strangers.forEach { addView(viewOfStranger(it)) }
				}
			}
		}
	}

	private fun fetchAndRefresh() {
		progressBar.visibility = VISIBLE
		doAsync {
			ChatApi.getContacts()
			ChatApi.getRequests()
			runOnUiThread {
				progressBar.visibility = GONE
				contactsView.run {
					removeAllViews()
					storage.requests.forEach { addView(viewOf(it)) }
					storage.contacts.forEach { addView(viewOf(it)) }
				}
			}
		}
	}

	private fun viewOf(contact: Contact) = UI {
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
				onClick { goTo(conversationFragment(contact), true, enterFromRight) }
			}
		}.apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			).apply { margin = dip(5) }
		}
	}.view

	private fun viewOf(friendRequest: FriendRequest) = UI {
		linearLayout {
			linearLayout {
				imageView(R.drawable.ic_baseline_person_add_24px).lparams(height = matchParent)
				textView(context.getString(R.string.request_from_) + friendRequest.username) {
					gravity = Gravity.CENTER
					textSize = 22f
					textColor = Color.BLACK
				}.lparams { margin = dip(5) }
			}.lparams { weight = 1f }
			button(R.string.accept) {
				onClick {
					doAsync {
						ChatApi.acceptRequest(friendRequest)
						runOnUiThread { fetchAndRefresh() }
					}
				}
			}
			button(R.string.decline) {
				onClick {
					doAsync {
						ChatApi.declineRequest(friendRequest)
						runOnUiThread { fetchAndRefresh() }
					}
				}
			}
		}.apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			).apply { margin = dip(5) }
		}
	}.view

	private fun viewOfStranger(name: String) = UI {
		linearLayout {
			linearLayout {
				imageView(R.drawable.ic_baseline_person_outline_24px).lparams(height = matchParent)
				textView(name) {
					gravity = Gravity.CENTER
					textSize = 22f
					textColor = Color.BLACK
				}.lparams { margin = dip(5) }
			}.lparams { weight = 1f }
			button(R.string.send_request) {
				onClick {
					doAsync {
						ChatApi.sendRequest(name)
						runOnUiThread {
							searchAndRefresh()
						}
					}
				}
			}
		}.apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			).apply { margin = dip(5) }
		}
	}.view

}