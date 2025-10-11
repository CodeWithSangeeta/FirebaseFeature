package com.practice.firebasefeature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        _authState.value = AuthState.Loading
        if(auth.currentUser != null){
            _authState.value =AuthState.Authenticated
        }else{
            _authState.value = AuthState.Unauthenticated


        }
    }

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

        // auth.signInWithEmailAndPassword(email,password)
//            .addOnCompleteListener{ task ->
//                if(task.isSuccessful){
//                    _authState.value = AuthState.Authenticated
//                }
//                else{
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
//                }
//            }
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

//        auth.createUserWithEmailAndPassword(email,password)
//            .addOnCompleteListener{ task ->
//                if(task.isSuccessful){
//                    _authState.value = AuthState.Authenticated
//                }
//                else{
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
//                }
  //          }


    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}


