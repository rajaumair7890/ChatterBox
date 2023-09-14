package com.codingwithumair.app.chatterbox

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codingwithumair.app.chatterbox.model.User
import com.codingwithumair.app.chatterbox.ui.screens.mainScreen.MainScreen
import com.codingwithumair.app.chatterbox.ui.screens.mainScreen.MainViewModel
import com.codingwithumair.app.chatterbox.ui.screens.signIn.SignInScreen
import com.codingwithumair.app.chatterbox.ui.screens.signIn.SignInViewModel
import com.codingwithumair.app.chatterbox.ui.screens.welcomeScreen.WelcomeScreen
import com.codingwithumair.app.chatterbox.ui.screens.welcomeScreen.WelcomeViewModel
import com.codingwithumair.app.chatterbox.ui.theme.ChatterBoxTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		FirebaseApp.initializeApp(this)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		super.onCreate(savedInstanceState)
		setContent {
			ChatterBoxTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {

					val navController = rememberNavController()

					NavHost(
						navController = navController,
						startDestination = NavDestinations.Welcome.name
					){

						composable(NavDestinations.Welcome.name){

							val viewModel = viewModel<WelcomeViewModel>(factory = WelcomeViewModel.factory)

							WelcomeScreen()

							if(viewModel.getSignedInUser() != null){
								navController.navigate(NavDestinations.MainScreen.name){
									popUpTo(NavDestinations.Welcome.name){
										inclusive = true
									}
								}
							}else{
								navController.navigate(NavDestinations.SignIn.name){
									popUpTo(NavDestinations.Welcome.name){
										inclusive = true
									}
								}
							}

						}

						composable(NavDestinations.SignIn.name) {

							val viewModel = viewModel<SignInViewModel>(factory = SignInViewModel.factory)
							val signInstate by viewModel.state.collectAsStateWithLifecycle()

							val launcher = rememberLauncherForActivityResult(
								contract = ActivityResultContracts.StartIntentSenderForResult(),
								onResult = { result ->

									if(result.resultCode == RESULT_OK) {
										lifecycleScope.launch {
											val signInResult = viewModel.signInWithIntent(
												intent = result.data ?: return@launch
											)
											viewModel.onSignInResult(
												result = signInResult,
												onSuccess = {
													navController.navigate(NavDestinations.MainScreen.name){
														popUpTo(NavDestinations.SignIn.name){
															inclusive = true
														}
													}
												}
											)
										}
									}

								}
							)

							LaunchedEffect(key1 = signInstate.isSignInSuccessful) {
								if(signInstate.isSignInSuccessful) {
									Toast.makeText(
										applicationContext,
										"Sign in successful",
										Toast.LENGTH_LONG
									).show()
								}
							}

							LaunchedEffect(key1 = signInstate.signInError) {
								signInstate.signInError?.let { error ->
									Toast.makeText(
										applicationContext,
										error,
										Toast.LENGTH_LONG
									).show()
								}
							}

							SignInScreen(
								isSigningIn = signInstate.isSigningIn,
								isError = signInstate.signInError != null,
								onSignInClick = {
									lifecycleScope.launch {
										val signInIntentSender = viewModel.signIn()
										launcher.launch(
											IntentSenderRequest.Builder(
												signInIntentSender ?: return@launch
											).build()
										)
									}
								}
							)
						}

						composable(NavDestinations.MainScreen.name) {

							val viewModel = viewModel<MainViewModel>(factory = MainViewModel.factory)

							MainScreen(
								chats = viewModel.listOfChats,
								currentChat = viewModel.currentChat,
								onChatClick = viewModel::updateCurrentChat,
								allMessages = viewModel.listOfMessages,
								messageBeingTyped = viewModel.message,
								updateMessageBeingTyped = viewModel::updateMessage,
								onSendMessageClick = viewModel::sendMessage,
								currentUser = viewModel.getSignedInUser() ?: User("", "", "", ""),
								onSearchUserClick = viewModel::searchForUsersByEmail,
								onUserIconClick = {},
								userSearchEmail = viewModel.userEmailForNewConversation,
								updateUserSearchEmail = viewModel::updateUserEmailForNewConversation,
								isError = viewModel.isError
							)

						}

					}
				}
			}
		}
	}
}

enum class NavDestinations{
	Welcome,
	SignIn,
	MainScreen
}
