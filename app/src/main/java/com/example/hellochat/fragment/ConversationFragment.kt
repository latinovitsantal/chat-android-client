package com.example.hellochat.fragment

import android.os.*
import android.support.v4.app.*
import android.support.v7.widget.*
import android.util.*
import android.view.*
import android.widget.*
import com.example.hellochat.*
import com.example.hellochat.R
import com.example.hellochat.data.*
import com.example.hellochat.data.Message
import com.example.hellochat.extension.*
import com.example.hellochat.network.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.*
import org.jetbrains.anko.support.v4.*
import java.lang.Exception

private var nextContact: Contact? = null
fun conversationFragment(contact: Contact): ConversationFragment {
	nextContact = contact
	return ConversationFragment()
}

class ConversationFragment : Fragment() {

	private lateinit var recyclerView: RecyclerView
	private val contact = nextContact!!
	private var isActive = false

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_conversation, container, false)
		view.findViewById<RecyclerView>(R.id.messagesRecyclerView).run {
			layoutManager = LinearLayoutManager(activity).apply {
				reverseLayout = true
			}
			adapter = MessageAdapter(contact.messages)
			recyclerView = this
		}
		recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
			if (!recyclerView.canScrollVertically(-1))
				getPrevMessages()
		}
		val editText = view.findViewById<EditText>(R.id.messageEditText)
		view.findViewById<Button>(R.id.sendButton).onClick {
			sendMessage(editText.text.getStringThenClear())
		}
		return view
	}

	override fun onStart() {
		super.onStart()
		getMessages()
	}

	override fun onResume() {
		super.onResume()
		isActive = true
		getNewMessages()
	}

	override fun onPause() {
		super.onPause()
		isActive = false
	}


	private fun goToBottom() {
		recyclerView.scrollToPosition(0)
	}


	private fun getMessages() {
		doAsync {
			ChatApi.getMessagesOf(contact)
			runOnUiThread {
				recyclerView.adapter!!.notifyDataSetChanged()
			}
		}
	}

	private fun getPrevMessages() {
		doAsync {
			val messageCount = contact.messages.size
			ChatApi.getPrevMessagesOf(contact)
			val messageCountDifference = contact.messages.size - messageCount
			if (messageCountDifference > 0) runOnUiThread {
				recyclerView.adapter!!.notifyItemRangeChanged(messageCount, messageCountDifference)
			}
		}
	}

	private fun getNewMessages() {
		doAsync {
			while (isActive) {
				Thread.sleep(500)
				val messageCount = contact.messages.size
				ChatApi.seeMessagesOf(contact)
				val messageCountDifference = contact.messages.size - messageCount
				val wasAtBottom = !recyclerView.canScrollVertically(1)
				if (messageCountDifference > 0) runOnUiThread {
					recyclerView.adapter!!.notifyItemRangeInserted(0, messageCountDifference)
					if (wasAtBottom)
						goToBottom()
				}
			}
		}
	}

	private fun sendMessage(message: String) {
		val messageToSend = message.trim()
		if (messageToSend.isNotEmpty()) {
			goToBottom()
			doAsync {
				ChatApi.sendMessage(message, contact)
			}
		}
	}

}


class MessageAdapter(private val messages: MutableList<Message>)
	: RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

	class ViewHolder(val messageLayout: LinearLayout) : RecyclerView.ViewHolder(messageLayout)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val messageLayout = LayoutInflater.from(parent.context)
			.inflate(R.layout.layout_message, parent, false) as LinearLayout
		return ViewHolder(messageLayout)
	}

	override fun getItemCount() = messages.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.messageLayout.run {
			val senderNameTextView = findViewById<TextView>(R.id.senderNameTextView)
			val messageTextView = findViewById<TextView>(R.id.messageEditText)
			messages[position].run {
				gravity = if (username == MainActivity.instance.storage.username) Gravity.END else Gravity.START
				senderNameTextView.text = username ?: ""
				messageTextView.text = text
			}
		}

	}

}