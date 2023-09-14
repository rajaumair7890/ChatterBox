package com.codingwithumair.app.chatterbox.ui.screens.mainScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingwithumair.app.chatterbox.ChatterBoxApplication
import com.codingwithumair.app.chatterbox.auth.GoogleAuthUiClient
import com.codingwithumair.app.chatterbox.firebase.FireStoreRepository
import com.codingwithumair.app.chatterbox.model.Chat
import com.codingwithumair.app.chatterbox.model.Message
import com.codingwithumair.app.chatterbox.model.MessageUiState
import com.codingwithumair.app.chatterbox.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainViewModel(
	private val fireStoreRepository: FireStoreRepository,
	private val googleAuthUiClient: GoogleAuthUiClient
): ViewModel(){

	var listOfChats by mutableStateOf(listOf<Chat>())

	var listOfMessages by mutableStateOf(listOf<MessageUiState>())

	var currentChat by mutableStateOf(Chat())

	init {
		getAllChats{chats ->
			if(chats.isNotEmpty()){
				Log.d("mainViewModelInit", "listOfChatIsNotEmpty")
				listOfChats = chats
				updateCurrentChat(listOfChats.first())
			}
		}
	}

	fun updateCurrentChat(chat: Chat){
		currentChat = chat
		getMessagesByChat(currentChat){ messages ->
			if(messages.isNotEmpty()){
				val signedInUser = getSignedInUser() ?: User()
				listOfMessages = messages.map{ message ->
					MessageUiState(
						content = message.content,
						sentBy = message.sentBy,
						isSentByCurrentUser = signedInUser.userId == message.sentBy.userId,
						dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.timeStamp), ZoneId.systemDefault()).format(
							DateTimeFormatter.ofPattern("d MMM yyy HH:mm")
						)
					)
				}
			}
		}
	}

	var isError by mutableStateOf(Pair<String, Exception?>("", null))

	fun getSignedInUser() = googleAuthUiClient.getSignedInUser()

	fun sendMessage(){

		val currentUser = getSignedInUser()

		if(currentUser != null){
			val receivedBy = if(currentChat.participants[0].userId == currentUser.userId) currentChat.participants[1] else currentChat.participants[0]
			fireStoreRepository.sendMessage(
				Message(
					content = message,
					sentBy = currentUser,
					receivedBy = receivedBy,
					participantsUid = listOf(currentUser.userId, receivedBy.userId),
					timeStamp = System.currentTimeMillis()
				),
				currentChat = currentChat,
				onSuccess = {
					message = ""
				},
				onFailure = {
					isError = Pair("sendMessage", it)
				}
			)
		}

	}

	private fun getAllChats(returnedChats: (List<Chat>) -> Unit){

		val signedInUser = getSignedInUser()

		if(signedInUser != null){
			fireStoreRepository.getAllChats(
				currentUser = signedInUser,
				returnedChats = returnedChats,
				onFailure = {
					isError = Pair("getAllChats", it)
				}
			)
		}
	}

	private fun getMessagesByChat(chat: Chat, returnedMessages: (List<Message>) -> Unit){
		fireStoreRepository.getAllMessages(
			chat = chat,
			returnedMessages = returnedMessages,
			onFailure = {
				isError = Pair("getMessagesByChat", it)
			}
		)
	}

	fun searchForUsersByEmail(){
		fireStoreRepository.searchForUsersByEmail(
			userEmail = userEmailForNewConversation,
			onSuccess = {
				startNewChat(it)
				userEmailForNewConversation = ""
			},
			onFailure = {
				isError = Pair("searchUsersByEmail", it)
				userEmailForNewConversation = ""
			}
		)
	}

	private fun startNewChat(
		user: User,
	){
		val signedInUser = getSignedInUser()

		if (signedInUser != null){
			val chat = Chat(
				participants = listOf(signedInUser, user),
				participantsUid = listOf(signedInUser.userId, user.userId),
				chatCreated = System.currentTimeMillis()
			)

			fireStoreRepository.startNewChat(
				chat = chat,
				onSuccess = {
					updateCurrentChat(chat)
					userEmailForNewConversation = ""
				},
				onFailure = {
					isError = Pair("startNewChat", it)
				}
			)
		}
	}

	var userEmailForNewConversation by mutableStateOf("")

	fun updateUserEmailForNewConversation(newEmail: String){
		userEmailForNewConversation = newEmail
	}

	var message by mutableStateOf("")

	fun updateMessage(newMessage: String) {
		message = newMessage
	}

	companion object{

		val factory = viewModelFactory {

			initializer {
				val application = (this[APPLICATION_KEY] as ChatterBoxApplication)
				MainViewModel(
					fireStoreRepository = application.appContainer.fireStoreRepository,
					googleAuthUiClient = application.appContainer.googleAuthUiClient
				)
			}

		}
	}

}
