package com.screens

import android.app.Activity
import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.practice.firebase.routes
import com.practice.firebasefeature.AuthState
import com.practice.firebasefeature.AuthViewModel
import com.practice.firebasefeature.R

@Composable
fun OtpVerification(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModels: AuthViewModel,
) {
    var phoneNo by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val authState by authViewModels.authState.observeAsState()
    val isSendingOtp by authViewModels.isSendingOtp.collectAsState(initial = false)
    var context = LocalContext.current


    // 🔁 React to authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                navController.navigate(routes.HomeScreen)
            }

            is AuthState.OtpSent -> {
                Toast.makeText(context, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "OTP Verification",
            fontSize = 42.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive
        )


        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.otp_verification_img),
            contentDescription = "Successfull Image",
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop

        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = phoneNo,
            onValueChange = { phoneNo = it },
            label = {
                Text(text = "Phone Number")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (phoneNo.isNotEmpty()) {
                    authViewModels.sendOtp(phoneNo, context as Activity)
                }
            },
            enabled = !isSendingOtp
        ) {
            if (isSendingOtp) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Send OTP",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive,
                    fontSize = 22.sp
                )
            }
            }





            Spacer(modifier = Modifier.height(40.dp))
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = {
                    Text(text = "Enter OTP")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = authState is AuthState.OtpSent
            )

            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {
                    if (otp.isNotEmpty()) {
                        authViewModels.verifyOtp(otp)
                    }
                },
                enabled = authState is AuthState.OtpSent
            ) {
                Text(
                    text = "Verify OTP",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive,
                    fontSize = 22.sp
                )
            }

//         Spacer(modifier = Modifier.height(16.dp))
//
//            when (val state = authState) {
//                is AuthState.Loading -> Text(
//                    "Please wait...",
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                is AuthState.Authenticated -> {
//                    LaunchedEffect(Unit) {
//                        navController.navigate(routes.HomeScreen)
//                    }
//                }
//
//                is AuthState.Error -> Text(
//                    "Error: ${state.message}",
//                    color = MaterialTheme.colorScheme.error
//                )
//
//                is AuthState.OtpSent -> Text("OTP Sent! Please check your messages.")
//                else -> {}
//            }


        }
    }

