package com.codingwithumair.app.chatterbox.ui.screens.mainScreen

import android.content.res.Resources.NotFoundException
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codingwithumair.app.chatterbox.R
import com.codingwithumair.app.chatterbox.model.Chat
import com.codingwithumair.app.chatterbox.model.Message
import com.codingwithumair.app.chatterbox.model.MessageUiState
import com.codingwithumair.app.chatterbox.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	chats: List<Chat>,
	currentChat: Chat,
	onChatClick: (Chat) -> Unit,
	allMessages: List<MessageUiState>,
	messageBeingTyped: String,
	updateMessageBeingTyped: (String) -> Unit,
	onSendMessageClick: () -> Unit,
	userSearchEmail: String,
	updateUserSearchEmail: (String) -> Unit,
	onUserIconClick: (User) -> Unit,
	onSearchUserClick: () -> Unit,
	currentUser: User,
	isError: Pair<String, Exception?>,
	modifier: Modifier = Modifier
){

	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

	val context = LocalContext.current

	val coroutineScope = rememberCoroutineScope()

	LaunchedEffect(key1 = isError) {
		if(isError.second != null){
			if(isError.second is NotFoundException){
				Toast.makeText(context, "No User with matching credentials!", Toast.LENGTH_LONG).show()
			}else if(isError.second?.message == "Chat Already Exists"){
				Toast.makeText(context, "Chat Already Exists", Toast.LENGTH_LONG).show()
			}else{
				val message = isError.first + isError.second?.message.toString()
				Log.d("MainScreen", message)
				isError.second?.printStackTrace()
				Toast.makeText(context, message, Toast.LENGTH_LONG).show()
			}

		}
	}

	ModalNavigationDrawer(
		drawerState = drawerState,
		gesturesEnabled = true,
		drawerContent = {
			ModalDrawerSheet(
				drawerShape = RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp),
				drawerTonalElevation = 12.dp
			) {
				LazyColumn{
					item{
						CustomTextField(
							value = userSearchEmail,
							onValueChange = updateUserSearchEmail,
							user = currentUser,
							onLeadingIconClick = onUserIconClick,
							onTrailingIconClick = onSearchUserClick,
							placeHolderText = stringResource(id = R.string.search_place_holder),
							containerColor = MaterialTheme.colorScheme.surface,
							modifier = Modifier
								.fillMaxWidth()
								.padding(12.dp)
						)
					}
					items(chats){chat ->
						ChatItem(
							chat = chat,
							onChatClick = onChatClick,
							currentChat = currentChat,
							currentUser = currentUser,
							modifier = Modifier.padding(8.dp)
						)
					}
				}
			}
		},
		modifier = modifier.navigationBarsPadding()
	) {
		Scaffold(
			modifier = modifier.fillMaxSize(),
			bottomBar = {
				CustomTextField(
					value = messageBeingTyped,
					onValueChange = {
						updateMessageBeingTyped(it)
					},
					placeHolderText = stringResource(id = R.string.message_place_holder),
					user = null,
					onLeadingIconClick = {},
					onTrailingIconClick = onSendMessageClick,
					containerColor = MaterialTheme.colorScheme.surfaceVariant,
					modifier = Modifier
						.fillMaxWidth()
						.padding(12.dp)
				)
			},
			topBar = {
				TopAppBar(
					title = {
						Text(text = stringResource(id = R.string.app_name))
					},
					navigationIcon = {
						IconButton(
							onClick = {
								coroutineScope.launch {
									drawerState.open()
								}
							}
						){
							Icon(Icons.Default.MoreVert, null)
						}
					}
				)

			}

			) {padding ->

			LazyColumn(
				modifier = Modifier
					.fillMaxSize(),
				contentPadding = padding,
				verticalArrangement = Arrangement.spacedBy(4.dp),
				reverseLayout = true
			) {
				items(allMessages) { message ->

					MessageItem(
						message = message,
						modifier = Modifier.padding(4.dp),
					)
				}
			}

		}

	}
}

@Composable
fun UserProfilePic(
	user: User,
	modifier: Modifier = Modifier
){
	Box(
		modifier = modifier
			.clip(CircleShape)
			.size(45.dp)
			.background(
				color = MaterialTheme.colorScheme.primaryContainer.copy(0.03f),
				shape = CircleShape
			)
			.border(1.dp, Color.LightGray, CircleShape)
		,
		contentAlignment = Alignment.Center
	){
		if (user.profilePictureUrl != null){
			AsyncImage(
				user.profilePictureUrl,
				contentScale = ContentScale.Crop,
				contentDescription = null,
				modifier = Modifier
					.clip(CircleShape)
					.matchParentSize()
			)
		}else{
			Text(
				text = user.userName?.first().toString().uppercase(),
				fontWeight = FontWeight.Bold,

			)
		}
	}
}

@Composable
fun MessageItem(
	message: MessageUiState,
	modifier: Modifier = Modifier
) {
	var expanded by remember { mutableStateOf(false) }

	Row(
		horizontalArrangement = if (message.isSentByCurrentUser) Arrangement.Start else Arrangement.End,
		verticalAlignment = Alignment.Top,
		modifier = modifier.fillMaxWidth()
	) {
		if(message.isSentByCurrentUser){
			UserProfilePic(user = message.sentBy)
		}
		ElevatedCard(
			shape = RoundedCornerShape(25.dp),
			modifier = modifier
				.clickable(onClick = {expanded = !expanded}),
			colors = CardDefaults.elevatedCardColors(
				containerColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
			),
			elevation = CardDefaults.elevatedCardElevation(4.dp),
		) {

			Text(
				text = message.content,
				textAlign =
					if (message.isSentByCurrentUser)
						TextAlign.End
					else
						TextAlign.Start,
				modifier = Modifier
					.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = if(expanded) 0.dp else 16.dp)
			)

			AnimatedVisibility(visible = expanded){
				Text(
					text = message.dateTime,
					fontSize = 10.sp,
					modifier = Modifier
						.padding(8.dp)
				)
			}
		}
		if(!message.isSentByCurrentUser){
			UserProfilePic(user = message.sentBy)
		}
	}

}

@Composable
fun CustomTextField(
	value: String,
	onValueChange: (String) -> Unit,
	placeHolderText: String,
	user: User?,
	onLeadingIconClick: (User) -> Unit,
	onTrailingIconClick: () -> Unit,
	containerColor: Color,
	modifier: Modifier = Modifier
){

	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		singleLine = true,
		placeholder = {
			Text(text = placeHolderText)
		},
		leadingIcon = {
			if(user != null){
				UserProfilePic(
					user = user,
					modifier = Modifier
						.padding(6.dp)
						.clickable(
							onClick = {
								onLeadingIconClick(user)
							}
						)
				)
			}else{
				Icon(
					imageVector = Icons.Default.MailOutline,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary
				)
			}
		},
		trailingIcon = {
			IconButton(onClick = onTrailingIconClick) {
				if(user != null){
					Icon(
						imageVector = Icons.Outlined.Search,
						contentDescription = placeHolderText,
						tint = MaterialTheme.colorScheme.primary
					)
				}else{
					Icon(
						imageVector = Icons.Outlined.Send,
						contentDescription = placeHolderText,
						tint = MaterialTheme.colorScheme.primary
					)
				}

			}
		},
		shape = CircleShape,
		colors = OutlinedTextFieldDefaults.colors(
			focusedContainerColor = containerColor,
			unfocusedContainerColor =  containerColor,
			focusedBorderColor = Color.Transparent,
			unfocusedBorderColor = Color.Transparent
		),
		modifier = modifier
	)
}

@Composable
fun ChatItem(
	chat: Chat,
	onChatClick: (Chat) -> Unit,
	currentChat: Chat,
	currentUser: User,
	modifier: Modifier = Modifier
){
	NavigationDrawerItem(
		label = {
			Text(
				text = if(chat.participants[0].userId == currentUser.userId) chat.participants[1].userName.toString() else chat.participants[0].userName.toString(),
				fontWeight = FontWeight.SemiBold
			)
		},
		icon = {
			UserProfilePic(
				user = if(chat.participants[0].userId == currentUser.userId) chat.participants[1] else chat.participants[0],
				modifier = Modifier.padding(4.dp)
			)
		},
		selected = chat == currentChat,
		onClick = {
			onChatClick(chat)
		},
		colors = NavigationDrawerItemDefaults.colors(
			selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
			selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
		),
		modifier = modifier
	)
}