package com.practice.firebasefeature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.practice.firebase.routes
import com.practice.firebase.routes.GoogleSignInFunction
import com.screens.EmailPasswordLogin
import com.screens.EmailPasswordSignup
import com.screens.GoogleSignInFunction
import com.screens.HomeScreen
import com.screens.OtpVerification
import com.screens.WelcomeScreen

@Composable
fun MyNavigation(modifier: Modifier = Modifier, authViewModels: AuthViewModel) {
    val navController = rememberNavController()

        NavHost(navController = navController, startDestination = routes.WelcomeScreen, builder ={
            composable(routes.WelcomeScreen){
                WelcomeScreen(modifier,navController,authViewModels)
            }
            composable(routes.EmailPasswordLogin){
                EmailPasswordLogin(modifier,navController,authViewModels)
            }
            composable(routes.EmailPasswordSignup){
                EmailPasswordSignup(modifier,navController,authViewModels)
            }
            composable(routes.HomeScreen){
                HomeScreen(modifier,navController,authViewModels)
            }
            composable(routes.GoogleSignInFunction){
                GoogleSignInFunction(modifier,navController,authViewModels)
            }
            composable(routes.OtpVerification){
                OtpVerification(modifier,navController,authViewModels)
            }
        })
    }