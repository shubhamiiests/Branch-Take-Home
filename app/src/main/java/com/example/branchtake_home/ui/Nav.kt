package com.example.branchtake_home.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.branchtake_home.ViewModel.AuthViewModel
import com.example.branchtake_home.ViewModel.ChatViewModel
import com.example.branchtake_home.ViewModel.ThreadsViewModel
import com.example.branchtake_home.ui.Screens.ChatScreen
import com.example.branchtake_home.ui.Screens.LoginScreen
import com.example.branchtake_home.ui.Screens.ThreadsScreen

object Routes {
    const val LOGIN = "login"
    const val THREADS = "threads"
    const val CHAT = "chat/{threadId}/{userId}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            val vm: AuthViewModel = hiltViewModel()
            LoginScreen(
                vm = vm,
                onSuccess = {
                    nav.navigate(Routes.THREADS) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.THREADS) {
            val vm: ThreadsViewModel = hiltViewModel()
            ThreadsScreen(
                vm = vm,
                onOpenThread = { threadId, userId ->
                    nav.navigate("chat/$threadId/$userId")
                },
                onLogout = {
                    nav.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument("threadId") { type = NavType.LongType },
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStack ->
            val threadId = backStack.arguments!!.getLong("threadId")
            val userId = backStack.arguments!!.getLong("userId")
            val vm: ChatViewModel = hiltViewModel()
            ChatScreen(threadId = threadId, userId = userId, vm = vm) {
                nav.popBackStack()
            }
        }
    }
}