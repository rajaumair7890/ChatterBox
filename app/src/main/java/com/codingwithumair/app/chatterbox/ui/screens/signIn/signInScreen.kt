package com.codingwithumair.app.chatterbox.ui.screens.signIn

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codingwithumair.app.chatterbox.R

@Composable
fun SignInScreen(
	isSigningIn: Boolean,
	isError: Boolean,
	onSignInClick: () -> Unit
) {
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

		Spacer(modifier = Modifier.size(48.dp))

		if(!isSigningIn){
			OutlinedButton(onClick = onSignInClick) {
				Image(painterResource(id = R.drawable.icons8_google_48), null)
				Spacer(modifier = Modifier.width(12.dp))
				Text(text = "Sign in")
			}
		}

		Spacer(modifier = Modifier.size(12.dp))

		if(isSigningIn){
			Text(
				stringResource(id = R.string.signing_in_message),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary,
			)
			Text(
				stringResource(id = R.string.please_wait),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary,
			)
			Spacer(modifier = Modifier.size(12.dp))
			LoadingAnimation()
		}

		if(isError){
			Text(
				text = stringResource(id = R.string.error_message_line_1),
				color = MaterialTheme.colorScheme.error
			)
			Text(
				text = stringResource(id = R.string.error_message_line_2),
				color = MaterialTheme.colorScheme.error
			)
			Text(
				text = stringResource(id = R.string.error_message_line_3),
				color = MaterialTheme.colorScheme.error
			)
			Text(
				text = stringResource(id = R.string.error_message_line_4),
				color = MaterialTheme.colorScheme.error
			)
		}
	}
}

@Composable
fun LoadingAnimation(
	modifier: Modifier = Modifier,
	colors: List<Color> = listOf(
		Color(0xFFF4B400),
		Color(0xFF0F9D58),
		Color(0xFFDB4437),
		Color(0xFF4285F4)
	),
	strokeWidth: Dp = 4.dp
) {
	val expansionDuration by remember { mutableStateOf(700) }
	val infiniteTransition = rememberInfiniteTransition(label = "")


	val currentColorIndex by infiniteTransition.animateValue(
		initialValue = 0,
		targetValue = colors.size,
		typeConverter = Int.VectorConverter,
		animationSpec = infiniteRepeatable(
			repeatMode = RepeatMode.Restart,
			animation = tween(
				durationMillis = 2*expansionDuration*colors.size,
				easing = LinearEasing
			)
		), label = ""
	)

	val progress by infiniteTransition.animateFloat(
		initialValue = 0.1f,
		targetValue = 0.8f,
		animationSpec = infiniteRepeatable(
			repeatMode = RepeatMode.Reverse,
			animation = tween(
				durationMillis = expansionDuration,
				easing = LinearEasing
			)
		), label = ""
	)

	val rotation by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			repeatMode = RepeatMode.Restart,
			animation = tween(
				durationMillis = expansionDuration,
				easing = LinearEasing
			)
		), label = ""
	)

	CircularProgressIndicator(
		modifier = modifier
			.rotate(rotation),
		progress = progress,
		color = colors[currentColorIndex],
		strokeWidth = strokeWidth
	)
}
