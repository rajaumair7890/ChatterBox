package com.codingwithumair.app.chatterbox.firebase

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.Log
import com.codingwithumair.app.chatterbox.model.Chat
import com.codingwithumair.app.chatterbox.model.Message
import com.codingwithumair.app.chatterbox.model.User
import com.codingwithumair.app.chatterbox.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FireStoreRepository() {

	private val fireStore = FirebaseFirestore.getInstance()

	fun sendMessage(
		message: Message,
		currentChat: Chat,
		onSuccess: () -> Unit,
		onFailure: (Exception) -> Unit,
	) {
		fireStore.collection(Constants.CONVERSATIONS)
			.whereEqualTo(Constants.PARTICIPANTS_USER_ID, currentChat.participantsUid)
			.get()
			.addOnSuccessListener{
				try {
					it.documents.first().reference.collection(Constants.MESSAGES)
						.document()
						.set(message)
						.addOnSuccessListener {
							onSuccess()
						}
						.addOnFailureListener { exception ->
							onFailure(exception)
						}
				}catch (e: NoSuchElementException){
					e.printStackTrace()
				}

			}
	}


	fun getAllMessages(
		chat: Chat,
		returnedMessages: (List<Message>) -> Unit,
		onFailure: (Exception) -> Unit
	){
		fireStore.collection(Constants.CONVERSATIONS)
			.whereEqualTo(Constants.PARTICIPANTS_USER_ID, chat.participantsUid)
			.get()
			.addOnSuccessListener {
				Log.d("getAllMessages", "onSuccessChatRetrieval")
				try {
					it.documents.first().reference.collection(Constants.MESSAGES)
						.orderBy(Constants.TIMESTAMP, Query.Direction.DESCENDING)
						.addSnapshotListener { querySnapshot, error ->


							if(error != null){
								Log.d("getAllMessages", "querySnapShotError")
								onFailure(error)
								return@addSnapshotListener
							}


							val list = mutableListOf<Message>()

							querySnapshot?.forEach{ queryDocumentSnapshot ->
								list.add(queryDocumentSnapshot.toObject(Message::class.java))
							}

							returnedMessages(list)

						}
				}catch (e: NoSuchElementException){
					Log.d("getAllMessages", "NoSuchElementExceptionCaught")
					e.printStackTrace()
					returnedMessages(listOf())
				}

			}.addOnFailureListener{
				Log.d("getAllMessages", "onFailureChatRetrieval")
			}

	}

	fun startNewChat(
		chat: Chat,
		onSuccess: () -> Unit,
		onFailure: (Exception) -> Unit,
	){
		fireStore.collection(Constants.CONVERSATIONS)
			.whereEqualTo(Constants.PARTICIPANTS_USER_ID, chat.participantsUid)
			.get()
			.addOnSuccessListener {
				if(it.documents.isEmpty()){
					fireStore.collection(Constants.CONVERSATIONS)
						.document()
						.set(chat)
						.addOnSuccessListener {
							onSuccess()
						}
						.addOnFailureListener {exception ->
							onFailure(exception)
						}
				}else{
					onFailure(Exception("Chat Already Exists"))
				}
			}.addOnFailureListener {
				onFailure(it)
			}

	}

	fun getAllChats(
		currentUser: User,
		returnedChats: (List<Chat>) -> Unit,
		onFailure: (Exception) -> Unit,
	){
		fireStore.collection(Constants.CONVERSATIONS)
			.whereArrayContainsAny(Constants.PARTICIPANTS_USER_ID, listOf(currentUser.userId))
			.addSnapshotListener {querySnapshot, error ->

				if(error != null){
					onFailure(error)
					return@addSnapshotListener
				}

				val list = mutableListOf<Chat>()

				querySnapshot?.forEach { queryDocumentSnapshot ->
					list.add( queryDocumentSnapshot.toObject(Chat::class.java) )
				}

				returnedChats(list)

			}
	}

	fun addNewUser(
		user: User,
		onSuccess: () -> Unit
	){
		fireStore.collection(Constants.USERS)
			.document(user.userId)
			.set( user )
			.addOnSuccessListener {
				onSuccess()
			}
	}

	fun searchForUsersByEmail(
		userEmail: String,
		onSuccess: (User) -> Unit,
		onFailure: (Exception) -> Unit
	){
		fireStore.collection(Constants.USERS)
			.whereEqualTo(Constants.USER_EMAIl, userEmail)
			.get()
			.addOnSuccessListener { querySnapshot ->
				try {
					val user = querySnapshot.documents.first().toObject(User::class.java)

					if(user != null){
						onSuccess(user)
					}else{
						onFailure(NotFoundException())
					}
				}catch (e: NoSuchElementException){
					onFailure(NotFoundException())
				}

			}.addOnFailureListener {
				onFailure(it)
			}
	}

}

