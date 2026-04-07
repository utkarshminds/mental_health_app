package com.example.utkarsh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utkarsh.ui.chat.ChatScreen
import com.example.utkarsh.ui.home.HomeScreen
import com.example.utkarsh.ui.login.ForgotPasswordScreen
import com.example.utkarsh.ui.login.LoginScreen
import com.example.utkarsh.ui.login.SignupScreen
import com.example.utkarsh.ui.splash.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UtkarshApp()
        }
    }
}

@Composable
fun UtkarshApp() {
    val navController = rememberNavController()
    LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onNavigateToSignup = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            SignupScreen(onBackToLogin = { navController.popBackStack() })
        }
        composable("forgot_password") {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }
        composable("home") {
            HomeScreen(onNavigateToChat = { navController.navigate("chat") })
        }
        composable("chat") {
            ChatScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
