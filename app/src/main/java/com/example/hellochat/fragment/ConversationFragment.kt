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
import com.example.hellochat.network.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.*
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
	private var messages = mutableListOf<Message>()


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_conversation, container, false)
		Log.i("hellochat", "onCreateView0")
		storage.conversations[contact]?.let { messages = it }
		view.findViewById<RecyclerView>(R.id.messagesRecyclerView).run {
			layoutManager = LinearLayoutManager(activity).apply {
				reverseLayout = true
			}
			adapter = MessageAdapter(messages)
			recyclerView = this
		}

		recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
			if (!recyclerView.canScrollVertically(-1))
				doAsync {
					val messagesCount = messages.size
					ChatApi.getPrevMessagesOf(contact)
					val messageCountDifference = messages.size - messagesCount
					if (messageCountDifference > 0) runOnUiThread {
						recyclerView.adapter!!.notifyItemRangeChanged(messagesCount, messageCountDifference)
					}
				}
		}

		view.findViewById<Button>(R.id.sendButton).run {
			onClick { recyclerView.scrollToPosition(0) }
		}
		Log.i("hellochat", "onCreateView")
		return view
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.i("hellochat", "onCreate")
	}

	override fun onStart() {
		super.onStart()
		doAsync {
			Log.i("hellochat", "beforeGet")
			ChatApi.getMessagesOf(contact)
			Log.i("hellochat", "afterGet")
			runOnUiThread {
				Log.i("hellochat", "beforeNotify")
				recyclerView.adapter!!.notifyDataSetChanged()
				Log.i("hellochat", "afterNotify")
			}
		}
		Log.i("hellochat", "onStart")
	}

}


class MessageAdapter(val messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

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
			val messageTextView = findViewById<TextView>(R.id.messageTextView)
			messages[position].run {
				gravity = if (username == MainActivity.instance.storage.username) Gravity.END else Gravity.START
				senderNameTextView.text = username ?: ""
				messageTextView.text = text
			}
		}

	}

}