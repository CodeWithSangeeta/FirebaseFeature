package com.practice.firebasefeature

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    //email-password-auth
    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

   //for google-sign-in
    lateinit var googleSignInClient: GoogleSignInClient



    //otp verification
    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _isSendingOtp = MutableStateFlow(false)
    val isSendingOtp = _isSendingOtp.asStateFlow()





    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser != null){
            _authState.value =AuthState.Authenticated
        }else{
            _authState.value = AuthState.Unauthenticated
        }
    }


    //Email-password-aut
    fun login(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and password can't be empty")
            return
        }
        _authState.value =AuthState.Loading

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            } catch(e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun signup(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and password can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            } catch(e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun signout(){
        auth.signOut()
        // 2️⃣ Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("SignOut", "Google Sign-out completed")
        }

        // Optional: revoke access (forces account selection next time)
        googleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d("SignOut", "Google access revoked")
        }

        _authState.value = AuthState.Unauthenticated
    }


    // Google SignIn client
    fun initGoogleSignIn(context: Context, webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)  // Web client ID from Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }


    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    Log.d("FirebaseAuth", "Google sign-in successful!")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Google Sign-in failed")
                    Log.e("FirebaseAuth", "Error: ${task.exception?.message}")
                }
            }
    }



                                //otp verification
    //Step1 :Send OTP
    fun sendOtp(
                phoneNumber: String,
                activity : Activity
            ) {
        _isSendingOtp.value = true
        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {  //com.google.firebase.auth.
                _isSendingOtp.value = false
                // Auto verification completed
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _isSendingOtp.value = false
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken) {
                _isSendingOtp.value = false
                _verificationId.value = verificationId
                _otpSent.value = true
                _authState.value = AuthState.OtpSent
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                _isSendingOtp.value = false
                _verificationId.value = verificationId
            }
        }

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }


    // Step 2: Verify OTP
    fun verifyOtp(otp: String) {
        val verificationId = _verificationId.value
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            _authState.value = AuthState.Error("Verification ID is null. Please resend OTP.")
        }
    }

    // Step 3: Sign in with the received credential
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isSendingOtp.value = false // Stop loading in all cases
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    _isAuthenticated.value = true
                    _otpSent.value = false
                    Log.d("AuthViewModel", "signInWithCredential:success")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign-in failed")
                    Log.e("AuthViewModel", "signInWithCredential:failure", task.exception)
                }
            }
    }

}


