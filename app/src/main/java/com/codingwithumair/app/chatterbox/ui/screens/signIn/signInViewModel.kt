package com.codingwithumair.app.chatterbox.ui.screens.signIn

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingwithumair.app.chatterbox.ChatterBoxApplication
import com.codingwithumair.app.chatterbox.auth.GoogleAuthUiClient
import com.codingwithumair.app.chatterbox.firebase.FireStoreRepository
import com.codingwithumair.app.chatterbox.model.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel(
	private val fireStoreRepository: FireStoreRepository,
	private val googleAuthUiClient: GoogleAuthUiClient
): ViewModel() {

	private val _state = MutableStateFlow( SignInUiState())
	val state = _state.asStateFlow()

	fun getSignedInUser() = googleAuthUiClient.getSignedInUser()

	suspend fun signInWithIntent(intent: Intent) = googleAuthUiClient.signInWithIntent(intent = intent)

	suspend fun signIn(): IntentSender?{
		_state.update {
			it.copy(
				isSigningIn = true
			)
		}
		return googleAuthUiClient.signIn()
	}

	fun onSignInResult(result: SignInResult, onSuccess: () -> Unit) {
		_state.update {
			it.copy(
				isSignInSuccessful = result.user != null,
				signInError = result.errorMessage
			)
		}
		if(result.user != null){
			val TAG = "SignIn ViewModel"
			Log.d(TAG, "user is not null")
			fireStoreRepository.addNewUser(
				user = result.user, onSuccess = {
					Log.d(TAG, "onSuccess Invoked")
					onSuccess()
				}
			)
		}
		if(result.errorMessage != null){
			_state.update {
				it.copy(
					isSigningIn = false
				)
			}
		}
	}

	fun resetState() {
		_state.update { SignInUiState() }
	}

	companion object{

		val factory = viewModelFactory {

			initializer {

				val application = (this[APPLICATION_KEY] as ChatterBoxApplication)
				SignInViewModel(
					fireStoreRepository = application.appContainer.fireStoreRepository,
					googleAuthUiClient = application.appContainer.googleAuthUiClient
				)

			}
		}
	}

}

data class SignInUiState(
	val isSignInSuccessful: Boolean = false,
	val isSigningIn: Boolean = false,
	val signInError: String? = null
)