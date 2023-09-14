package com.codingwithumair.app.chatterbox.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CancellationException
import com.codingwithumair.app.chatterbox.R
import com.codingwithumair.app.chatterbox.firebase.FireStoreRepository
import com.codingwithumair.app.chatterbox.model.SignInResult
import com.codingwithumair.app.chatterbox.model.User
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
	private val context: Context,
	private val oneTapClient: SignInClient,
) {

	private val auth = Firebase.auth

	suspend fun signIn(): IntentSender?{
		val result = try {
			oneTapClient.beginSignIn(
				buildSignInRequest()
			).await()
		}catch(e: Exception){
			e.printStackTrace()
			if(e is CancellationException) throw e
			null
		}
		return result?.pendingIntent?.intentSender
	}


	suspend fun signInWithIntent(intent: Intent): SignInResult{
		val credential = oneTapClient.getSignInCredentialFromIntent(intent)
		val googleIdToken = credential.googleIdToken
		val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
		return try {
			val user = auth.signInWithCredential(googleCredentials).await().user
			SignInResult(
				user = user?.run {
					User(
						userId = uid,
						userEmail = email,
						userName = displayName,
						profilePictureUrl = if(photoUrl != null) photoUrl.toString() else null
					)
				},
				errorMessage = null
			)
		}catch (e: Exception){
			e.printStackTrace()
			if (e is CancellationException) throw e
			SignInResult(
				user = null,
				errorMessage = e.message
			)
		}
	}

	private fun buildSignInRequest(): BeginSignInRequest{
		return BeginSignInRequest
			.Builder()
			.setGoogleIdTokenRequestOptions(
				GoogleIdTokenRequestOptions.builder()
					.setSupported(true)
					.setFilterByAuthorizedAccounts(false)
					.setServerClientId(context.getString(R.string.web_client_id))
					.build()
			)
			.setAutoSelectEnabled(true)
			.build()
	}

	fun getSignedInUser(): User? = auth.currentUser?.run {
		User(
			userId = uid,
			userEmail = email,
			userName = displayName,
			profilePictureUrl = photoUrl?.toString()
		)
	}


	suspend fun signOut(){
		try {
			oneTapClient.signOut().await()
		}catch (e: Exception){
			e.printStackTrace()
			if(e is CancellationException) throw e
		}
	}
}



