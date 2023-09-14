package com.codingwithumair.app.chatterbox.model

data class Chat(
	val participants: List<User> = emptyList(),
	val participantsUid: List<String> = emptyList(),
	val chatCreated: Long = 0,
)

data class Message(
	val content: String = "",
	val sentBy: User = User(),
	val receivedBy: User = User(),
	val participantsUid: List<String> = emptyList(),
	val timeStamp: Long = 0
)

data class MessageUiState(
	val content: String,
	val sentBy: User,
	val isSentByCurrentUser: Boolean,
	val dateTime: String
)

data class SignInResult(
	val user: User? = null,
	val errorMessage: String? = ""
)

data class User(
	val userId: String = "",
	val userEmail: String? = null,
	val userName: String? = null,
	val profilePictureUrl: String? = null
)