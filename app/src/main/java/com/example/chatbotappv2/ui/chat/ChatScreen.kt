package com.example.chatbotappv2.ui.chat

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.chatbotappv2.R
import com.example.chatbotappv2.data.FakeData
import com.example.compose.ChatBotTheme
import kotlinx.coroutines.launch

private const val TAG = "ChatScreen"

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
) {
    val chatUiState by chatViewModel.chatUiState.collectAsState()
    val context = LocalContext.current
    ChatScreen(
        modifier = modifier,
        onInputChange = chatViewModel::onInputChange,
        chatUiState = chatUiState,
        messageSend = { chatViewModel.chatWithGemini(context) },
        errorShown = chatViewModel::errorShown,
        generateThumbnail = chatViewModel::generateThumbnail,
        selectMedia = chatViewModel::setSelectedMediaUri,
    )
}

@Composable
internal fun ChatScreen(
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit,
    chatUiState: ChatUiState,
    messageSend: () -> Unit,
    errorShown: () -> Unit,
    generateThumbnail: (Context, Uri) -> Unit,
    selectMedia: (Uri) -> Unit
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    chatUiState.errorMessage?.let {
        scope.launch {
            val result = snackBarHostState
                .showSnackbar(
                    message = it,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    /* Handle snackbar action performed */
                    Log.i(TAG, "Snackbar action performed")
                }

                SnackbarResult.Dismissed -> {
                    /* Handle snackbar dismissed */
                    Log.i(TAG, "Snackbar dismissed")
                }
            }
            errorShown()
        }
    }

    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.safeDrawing), // Adjust for keyboard and status bar,
        topBar = {
            ChatTopBar()
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            Column(
                modifier = Modifier
                    .padding(mediumPadding)
            ) {
                MessageList(
                    modifier = Modifier.weight(1f),
                    messageList = chatUiState.messageList
                )
                MessageInputLayout(
                    input = chatUiState.input,
                    onInputChange = onInputChange,
                    isLoading = chatUiState.isLoading,
                    messageSend = messageSend,
                    generateThumbnail = generateThumbnail,
                    selectMedia = selectMedia,
                    chatUiState = chatUiState
                )
            }
        }
    }
}

@Composable
internal fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<Message>
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        items(messageList.reversed()) {
            MessageItem(
                message = it,
                isUser = it.role == Role.user
            )
        }
    }
}

@Composable
internal fun MessageInputLayout(
    modifier: Modifier = Modifier,
    input: String,
    onInputChange: (String) -> Unit,
    isLoading: Boolean,
    messageSend: () -> Unit,
    selectMedia: (Uri) -> Unit,
    generateThumbnail: (Context, Uri) -> Unit,
    chatUiState: ChatUiState
) {
    val smallPadding = dimensionResource(R.dimen.padding_small)
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectMedia(it)

            // Check if the selected media is a video
            val mimeType = context.contentResolver.getType(it)
            if (mimeType?.startsWith("video") == true) {
                generateThumbnail(context, it)
            }
        }
    }

    Card {
        Row(
            modifier = Modifier.padding(bottom = smallPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(smallPadding)
        ) {
            IconButton(
                onClick = { mediaPickerLauncher.launch("image/* video/*") }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                onValueChange = onInputChange,
                value = input,
                maxLines = 2,
                label = { Text(text = stringResource(R.string.ask_anything)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    messageSend()
                    keyboardController?.hide() // Hides the keyboard
                })
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                IconButton(
                    onClick = {
                        messageSend()
                        keyboardController?.hide() // Hides the keyboard
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                    )
                }
            }
        }

        chatUiState.selectedMediaUri?.let { uri ->
            if (chatUiState.thumbnail != null) {
                // Display video thumbnail
                Image(
                    bitmap = chatUiState.thumbnail.asImageBitmap(),
                    contentDescription = "Selected Video Thumbnail",
                    modifier = Modifier
                        .padding(bottom = smallPadding, start = smallPadding)
                        .size(80.dp)
                        .clickable { /* Full screen view or more actions */ },
                    contentScale = ContentScale.Crop
                )
            } else {
                // Display image preview
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .padding(bottom = smallPadding, start = smallPadding)
                        .size(80.dp)
                        .clickable { /* Full screen view or more actions */ },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
internal fun MessageItem(
    modifier: Modifier = Modifier,
    message: Message,
    isUser: Boolean
) {
    val smallPadding = dimensionResource(R.dimen.padding_small)
    val cardContainerColor =
        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val cardContentColor =
        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(if (isUser) Alignment.End else Alignment.Start)
            .padding(
                start = if (isUser) 70.dp else 0.dp,
                end = if (isUser) 0.dp else 70.dp,
                top = smallPadding,
                bottom = smallPadding
            ),
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor,
            contentColor = cardContentColor
        )
    ) {
        SelectionContainer {
            Text(
                modifier = Modifier
                    .padding(smallPadding),
                text = message.message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatTopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(stringResource(R.string.chat_bot))
        }
    )
}

@Preview(name = "ChatScreenPreview", showBackground = true)
@Composable
internal fun ChatScreenPreview() {
    ChatBotTheme {
        ChatScreen(
            chatUiState = ChatUiState(messageList = FakeData.loadMessageList()),
            onInputChange = {},
            messageSend = {},
            errorShown = {},
            generateThumbnail = { _, _ -> },
            selectMedia = {}
        )
    }
}

@Preview(name = "ChatScreenEmptyPreview", showBackground = true)
@Composable
internal fun ChatScreenEmptyPreview() {
    ChatBotTheme {
        ChatScreen(
            chatUiState = ChatUiState(),
            onInputChange = {},
            messageSend = {},
            errorShown = {},
            generateThumbnail = { _, _ -> },
            selectMedia = {}
        )
    }
}

@Preview(name = "ChatScreenDarkPreview", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun ChatScreenDarkPreview() {
    ChatBotTheme(darkTheme = true) {
        ChatScreen(
            chatUiState = ChatUiState(messageList = FakeData.loadMessageList()),
            onInputChange = {},
            messageSend = {},
            errorShown = {},
            generateThumbnail = { _, _ -> },
            selectMedia = {}
        )
    }
}

@Preview(name = "ChatScreenLoadingPreview", showBackground = true)
@Composable
internal fun ChatScreenLoadingPreview() {
    ChatBotTheme {
        ChatScreen(
            chatUiState = ChatUiState(isLoading = true),
            onInputChange = {},
            messageSend = {},
            errorShown = {},
            generateThumbnail = { _, _ -> },
            selectMedia = {}
        )
    }
}

@Preview(name = "ChatScreenErrorPreview", showBackground = true)
@Composable
internal fun ChatScreenErrorPreview() {
    ChatBotTheme {
        ChatScreen(
            chatUiState = ChatUiState(errorMessage = "Something went wrong"),
            onInputChange = {},
            messageSend = {},
            errorShown = {},
            generateThumbnail = { _, _ -> },
            selectMedia = {}
        )
    }
}