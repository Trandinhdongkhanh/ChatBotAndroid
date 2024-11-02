package com.example.chatbotappv2.ui.signin

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatbotappv2.R
import com.example.compose.ChatBotTheme
import kotlinx.coroutines.launch

private const val TAG = "SignInScreen"

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit,
    signInViewModel: SignInViewModel = viewModel(factory = SignInViewModel.Factory),
    onUserSignedIn: () -> Unit,
) {
    val signInUiState by signInViewModel.signInUiState.collectAsState()
    SignInScreen(
        modifier = modifier,
        signInUiState = signInUiState,
        onSignUpClick = onSignUpClick,
        onUserSignedIn = onUserSignedIn,
        errorShown = signInViewModel::errorShown,
        onUsernameChange = signInViewModel::onUsernameChange,
        onPasswordChange = signInViewModel::onPasswordChange,
        onSignInClick = signInViewModel::onSignInClick
    )
}

//For preview
//This compose is stateless
@Composable
internal fun SignInScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit,
    onUserSignedIn: () -> Unit,
    signInUiState: SignInUiState,
    errorShown: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    //Option 1
    if (signInUiState.isLoggedIn) {
        onUserSignedIn()
        return
    }

    //Option 2
//    LaunchedEffect(signInUiState) {
//        if (signInUiState.isLoggedIn){
//            onUserSignedIn()
//            return@LaunchedEffect
//        }
//    }

    if (signInUiState.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .size(150.dp)
        )
        return
    }

    signInUiState.error?.let {
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
            .windowInsetsPadding(WindowInsets.safeDrawing), // Adjust for keyboard and status bar,,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(mediumPadding)
            ) {
                Image(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .border(
                            width = 5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    painter = painterResource(R.drawable.chat_bot_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.chat_bot),
                    style = MaterialTheme.typography.displaySmall
                )
                InputLayout(
                    modifier = Modifier,
                    username = signInUiState.username,
                    password = signInUiState.password,
                    onUsernameChange = onUsernameChange,
                    onPasswordChange = onPasswordChange
                )
                ButtonLayout(
                    modifier = Modifier,
                    onSignInClick = onSignInClick,
                    onSignUpClick = onSignUpClick
                )
            }
        }
    }
}


@Composable
internal fun ButtonLayout(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(mediumPadding)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onSignInClick
        ) {
            Text(text = stringResource(R.string.sign_in))
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onSignUpClick
        ) {
            Text(text = stringResource(R.string.sign_up))
        }
    }
}

@Composable
internal fun InputLayout(
    modifier: Modifier = Modifier,
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = onUsernameChange,
            label = {
                Text(text = stringResource(R.string.username))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(text = stringResource(R.string.password))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Preview(name = "SignInPreview", showBackground = true)
@Composable
internal fun SignInPreview() {
    ChatBotTheme(darkTheme = false) {
        SignInScreen(
            modifier = Modifier,
            onSignUpClick = {},
            onUserSignedIn = {},
            signInUiState = SignInUiState(),
            errorShown = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSignInClick = {}
        )
    }
}

@Preview("SignInDarkThemePreview", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun SignInDarkThemePreview() {
    ChatBotTheme(darkTheme = true) {
        SignInScreen(
            modifier = Modifier,
            onSignUpClick = {},
            onUserSignedIn = {},
            signInUiState = SignInUiState(),
            errorShown = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSignInClick = {}
        )
    }
}

@Preview(name = "SignInLoadingPreview", showBackground = true)
@Composable
internal fun SignInLoadingPreview() {
    ChatBotTheme(darkTheme = false) {
        SignInScreen(
            modifier = Modifier,
            onSignUpClick = {},
            onUserSignedIn = {},
            signInUiState = SignInUiState(isLoading = true),
            errorShown = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSignInClick = {}
        )
    }
}

@Preview(name = "SignInFailedPreview", showBackground = true)
@Composable
internal fun SignInFailedPreview() {
    ChatBotTheme(darkTheme = false) {
        SignInScreen(
            modifier = Modifier,
            onSignUpClick = {},
            onUserSignedIn = {},
            signInUiState = SignInUiState(error = "Sign in failed"),
            errorShown = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSignInClick = {}
        )
    }
}