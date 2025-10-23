package com.example.branchtake_home.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kukufm.branchsupport.ui.screens.ChatScreen
import com.kukufm.branchsupport.ui.screens.LoginScreen
import com.kukufm.branchsupport.ui.screens.ThreadsScreen
import com.kukufm.branchsupport.ui.vm.AuthViewModel
import com.kukufm.branchsupport.ui.vm.ChatViewModel
import com.kukufm.branchsupport.ui.vm.ThreadsViewModel

object Routes {
    const val LOGIN = "login"
    const val THREADS = "threads"
    const val CHAT = "chat/{threadId}/{userId}"
}

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