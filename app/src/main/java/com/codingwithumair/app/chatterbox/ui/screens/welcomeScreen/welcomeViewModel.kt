package com.codingwithumair.app.chatterbox.ui.screens.welcomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingwithumair.app.chatterbox.ChatterBoxApplication
import com.codingwithumair.app.chatterbox.auth.GoogleAuthUiClient

class WelcomeViewModel(
	private val googleAuthUiClient: GoogleAuthUiClient
): ViewModel(){

	fun getSignedInUser() = googleAuthUiClient.getSignedInUser()

	companion object{
		val factory = viewModelFactory {
			initializer {
				val application = (this[APPLICATION_KEY] as ChatterBoxApplication)
				WelcomeViewModel(application.appContainer.googleAuthUiClient)
			}
		}
	}
}