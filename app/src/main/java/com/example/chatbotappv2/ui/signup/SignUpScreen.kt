package com.example.chatbotappv2.ui.signup

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.res.dimensionResource
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

private const val TAG = "SignUpScreen"

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel()
) {
    val signUpUiState by signUpViewModel.signUpUiState.collectAsState()
    SignUpScreen(
        onCancelClick = onCancelClick,
        signUpUiState = signUpUiState,
        onUsernameChange = signUpViewModel::onUsernameChange,
        onPasswordChange = signUpViewModel::onPasswordChange,
        onFullNameChange = signUpViewModel::onFullNameChange,
        onConfirmClick = signUpViewModel::onConfirmClick,
        errorShown = signUpViewModel::errorShown,
        reset = signUpViewModel::reset
    )
}

@Composable
internal fun SignUpScreen(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
    signUpUiState: SignUpUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    errorShown: () -> Unit,
    reset: () -> Unit
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    if (signUpUiState.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .size(150.dp)
        )
        return
    }
    signUpUiState.errorMessage?.let {
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
    if (signUpUiState.isSignedUp) {
        scope.launch {
            val result = snackBarHostState
                .showSnackbar(
                    message = "Sign up successfully",
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
            reset()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(mediumPadding)
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    style = MaterialTheme.typography.displaySmall
                )
                InputLayout(
                    username = signUpUiState.username,
                    password = signUpUiState.password,
                    fullName = signUpUiState.fullName,
                    onUsernameChange = onUsernameChange,
                    onPasswordChange = onPasswordChange,
                    onFullNameChange = onFullNameChange
                )
                ButtonLayout(
                    onConfirmClick = onConfirmClick,
                    onCancelClick = onCancelClick
                )
            }
        }
    }
}

@Composable
internal fun ButtonLayout(
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(mediumPadding)
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onConfirmClick
        ) {
            Text(text = stringResource(R.string.confirm))
        }
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onCancelClick
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}

@Composable
internal fun InputLayout(
    modifier: Modifier = Modifier,
    username: String,
    password: String,
    fullName: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = username,
            onValueChange = onUsernameChange,
            label = {
                Text(text = stringResource(R.string.username))
            },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(text = stringResource(R.string.password))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = fullName,
            onValueChange = onFullNameChange,
            label = {
                Text(text = stringResource(R.string.full_name))
            },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
            )
        )
    }
}

@Preview(showBackground = true, name = "SignUpPreview")
@Composable
internal fun SignUpPreview() {
    ChatBotTheme(darkTheme = false) {
        SignUpScreen(
            onCancelClick = {},
            signUpUiState = SignUpUiState(),
            onUsernameChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onConfirmClick = {},
            errorShown = {},
            reset = {}
        )
    }
}

@Preview(showBackground = true, name = "SignUpDarkThemePreview", uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun SignUpDarkThemePreview() {
    ChatBotTheme(darkTheme = true) {
        SignUpScreen(
            onCancelClick = {},
            signUpUiState = SignUpUiState(),
            onUsernameChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onConfirmClick = {},
            errorShown = {},
            reset = {}
        )
    }
}

@Preview(showBackground = true, name = "SignUpLoadingPreview")
@Composable
internal fun SignUpLoadingPreview() {
    ChatBotTheme(darkTheme = false) {
        SignUpScreen(
            onCancelClick = {},
            signUpUiState = SignUpUiState(isLoading = true),
            onUsernameChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onConfirmClick = {},
            errorShown = {},
            reset = {}
        )
    }
}

@Preview(showBackground = true, name = "SignUpFailedPreview")
@Composable
internal fun SignUpFailedPreview() {
    ChatBotTheme(darkTheme = false) {
        SignUpScreen(
            onCancelClick = {},
            signUpUiState = SignUpUiState(errorMessage = "Something went wrong"),
            onUsernameChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onConfirmClick = {},
            errorShown = {},
            reset = {}
        )
    }
}