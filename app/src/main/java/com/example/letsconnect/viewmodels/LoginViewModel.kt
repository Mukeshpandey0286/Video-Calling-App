package com.example.letsconnect.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Normal)
    val loginStateFlow = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        // Reset state to Normal
        _loginState.value = LoginState.Normal

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = LoginState.Error("Invalid email format")
            return
        }

        // Validate password length
        if (password.length < 6) {
            _loginState.value = LoginState.Error("Password must be at least 6 characters")
            return
        }

        val auth = FirebaseAuth.getInstance()
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                    _loginState.value = LoginState.Success
                        Log.d("LoginViewModel", "Login successful ${user.email}")
                    } else {
                        LoginState.Error("User is not find!")
                        Log.d("LoginViewModel", "Login failed! not found ")
                    }
                } else {
                    _loginState.value = LoginState.Error(task.exception?.localizedMessage ?: "Login failed")
                    Log.e("LoginViewModel", "SignInWithEmailAndPassword failed", task.exception)
                }
            } .addOnFailureListener { exception ->
                _loginState.value = LoginState.Error(exception.localizedMessage ?: "An error occurred")
                Log.e("LoginViewModel", "SignInWithEmailAndPassword failed", exception)
            }
    }
}



sealed class LoginState{
    object Loading: LoginState()
    data class Error(val message: String): LoginState()
    object Success: LoginState()
    object Normal : LoginState()

}