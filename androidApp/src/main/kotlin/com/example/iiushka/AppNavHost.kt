package com.example.iiushka

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

@Serializable
data object ChatRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = WelcomeRoute
    ) {
        composable<WelcomeRoute> {
            WelcomeScreen(
                onNavigateToChat = {
                    navController.navigate(ChatRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<ChatRoute> {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
