package com.example.chatbotappv2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatbotappv2.ui.chat.ChatScreen
import com.example.chatbotappv2.ui.signin.SignInScreen
import com.example.chatbotappv2.ui.signup.SignUpScreen


@Composable
fun ChatBotNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.SIGN_IN.name
    ) {
        composable(route = Screen.SIGN_IN.name) {
            SignInScreen(
                onSignUpClick = { navController.navigate(Screen.SIGN_UP.name) },
                onUserSignedIn = { navController.navigate(Screen.CHAT.name) },
            )
        }
        composable(route = Screen.SIGN_UP.name) {
            SignUpScreen(
                onCancelClick = { navController.popBackStack() }
            )
        }
        composable(route = Screen.CHAT.name) {
            ChatScreen()
        }
    }
}

enum class Screen {
    SIGN_IN, SIGN_UP, CHAT
}