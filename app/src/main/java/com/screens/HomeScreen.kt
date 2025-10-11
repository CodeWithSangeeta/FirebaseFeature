package com.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.practice.firebase.routes
import com.practice.firebasefeature.AuthViewModel
import com.practice.firebasefeature.R

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModels: AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Screen",
            fontSize = 42.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive
        )


        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.successfull_img),
            contentDescription = "Successfull Image",
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop

        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = {
            authViewModels.signout()
            navController.navigate(routes.EmailPasswordLogin)
        }){
            Text(text = "Sign Out",
                modifier= Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp)
        }

        Spacer(modifier=Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate(routes.WelcomeScreen)
        }){
            Text(text = "Back To Welcome Screen",
                modifier= Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp)
        }
    }
    
}