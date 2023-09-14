package com.codingwithumair.app.chatterbox.ui.screens.welcomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.codingwithumair.app.chatterbox.R

@Composable
fun WelcomeScreen(){
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f)),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			stringResource(id = R.string.app_name),
			style = MaterialTheme.typography.displayLarge,
			color = MaterialTheme.colorScheme.primary
		)
	}
}
