package com.example.hellochat.data


data class Contact(
	var id: Long,
	var username: String?,
	var unseenCount: Int,
	var lastUpdated: String
) {
	override fun equals(other: Any?) = when (other) {
		is Contact -> id == other.id
		else -> false
	}
	override fun hashCode() = id.hashCode()
}

data class Message(
	var id: Long,
	var text: String,
	var time: String,
	var username: String?,
	var isOwned: Boolean
) {
	override fun equals(other: Any?) = when (other) {
		is Message -> id == other.id
		else -> false
	}
	override fun hashCode() = id.hashCode()
}


fun Contact.refreshFrom(contact: Contact) {
	username = contact.username
	unseenCount = contact.unseenCount
	lastUpdated = contact.lastUpdated
}