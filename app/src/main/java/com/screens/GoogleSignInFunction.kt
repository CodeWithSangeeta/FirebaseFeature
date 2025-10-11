package com.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.practice.firebasefeature.AuthViewModel
import com.practice.firebasefeature.R
import com.google.android.gms.common.api.ApiException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import com.practice.firebase.routes
import com.practice.firebasefeature.AuthState

@Composable
fun GoogleSignInFunction(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModels: AuthViewModel
) {
    val context = LocalContext.current
    val webClientId = "463715601773-qa7bshhdmm1k6nim1gv5mm67n8ebablc.apps.googleusercontent.com"
    val authState = authViewModels.authState.observeAsState()

    LaunchedEffect(Unit) {
        authViewModels.initGoogleSignIn(context, webClientId)
    }
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate(routes.HomeScreen)
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }


// 🔹 Step 1: Launcher to handle Google Sign-In result
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    authViewModels.firebaseAuthWithGoogle(token)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error: ${e.message}")
            }
        }
    }



    Column(
        modifier = Modifier.fillMaxSize()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Google Sign-In",
            fontSize = 42.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive
        )


        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.google_sign_in_img),
            contentDescription = "Successfull Image",
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop

        )

        Spacer(modifier = Modifier.height(60.dp))
        // 🔹 Step 2: UI Button to trigger Google Sign-In flow
        Button(onClick = {
            val signInIntent = authViewModels.googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text(
                text = "Sign in with Google",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp
            )
        }
    }
}