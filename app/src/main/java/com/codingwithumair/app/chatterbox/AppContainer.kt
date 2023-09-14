package com.codingwithumair.app.chatterbox

import android.content.Context
import com.codingwithumair.app.chatterbox.auth.GoogleAuthUiClient
import com.codingwithumair.app.chatterbox.firebase.FireStoreRepository
import com.google.android.gms.auth.api.identity.Identity

class AppContainer(
	private val context: Context
){

	val fireStoreRepository by lazy {
		FireStoreRepository()
	}

	val googleAuthUiClient by lazy {
		GoogleAuthUiClient(
			context = context,
			oneTapClient = Identity.getSignInClient(context)
		)
	}

}