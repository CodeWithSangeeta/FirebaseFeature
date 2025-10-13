package com.practice.firebasefeature

import com.practice.firebasefeature.AuthState


sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message : String) : AuthState()
        object OtpSent : AuthState()

    }