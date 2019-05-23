package com.example.hellochat.data


data class Contact(
	var id: Long,
	var username: String?,
	var unseenCount: Int,
	var lastUpdated: String
) {
	var messages = mutableListOf<Message>()
}

data class Message(
	var id: Long,
	var text: String,
	var time: String,
	var username: String?,
	var isOwned: Boolean
)

data class FriendRequest(var username: String, var time: String)

data class PostMessage(val id: Long, val text: String)


fun Contact.refreshFrom(contact: Contact) {
	username = contact.username
	unseenCount = contact.unseenCount
	lastUpdated = contact.lastUpdated
}