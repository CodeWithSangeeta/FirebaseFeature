package com.screens

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.practice.firebase.routes
import com.practice.firebasefeature.AuthState
import com.practice.firebasefeature.AuthViewModel
import com.practice.firebasefeature.R

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModels: AuthViewModel = viewModel()
) {
    val authState by authViewModels.authState.observeAsState(AuthState.Loading)
    var isChecking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(34.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to AuthFlow",
            fontSize = 38.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive
            )
        Spacer(modifier = Modifier.height(45.dp))

        Image(
            painter = painterResource(id = R.drawable.welcome_screen_img),
            contentDescription = "Welcome Image",
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop

        )

         Spacer(modifier = Modifier.height(50.dp))


        Button(onClick = {
            isChecking = true
            authViewModels.checkAuthStatus()
            when (authState){
                is AuthState.Authenticated -> {
                    navController.navigate(routes.HomeScreen)
                }
                is AuthState.Unauthenticated,
                is AuthState.Error-> {
                    navController.navigate(routes.EmailPasswordLogin)
                }
                else -> {}
            }
            isChecking = false
        }){
            Text(text = "Email & Password Login",
                modifier= Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp)
        }

        // 🔹 Show Loading Indicator only during check
        if (isChecking || authState is AuthState.Loading) {
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator()
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(routes.GoogleSignIn)
        }){
            Text(text = "Google Sign In",
                modifier= Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(routes.OtpVerification)
        }){
            Text(text = "OTP Verification",
                modifier= Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp)
        }

    }

}