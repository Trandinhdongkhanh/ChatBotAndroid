package com.example.chatbotappv2.ui.chat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    ChatScreen(
        modifier = modifier,
        onInputChange = chatViewModel::onInputChange,
        chatUiState = chatUiState,
        messageSend = chatViewModel::sendMessage,
        errorShown = chatViewModel::errorShown
    )
}

@Composable
internal fun ChatScreen(
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit,
    chatUiState: ChatUiState,
    messageSend: () -> Unit,
    errorShown: () -> Unit
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
                    messageSend = messageSend
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
    messageSend: () -> Unit
) {
    val smallPadding = dimensionResource(R.dimen.padding_small)
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
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
            return
        }
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
            errorShown = {}
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
            errorShown = {}
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
            errorShown = {}
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
            errorShown = {}
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
            errorShown = {}
        )
    }
}