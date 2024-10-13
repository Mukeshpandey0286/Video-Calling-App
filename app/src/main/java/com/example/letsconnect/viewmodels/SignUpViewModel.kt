package com.example.letsconnect.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val signUpState = MutableStateFlow<SignUpState>(SignUpState.Normal)
    val signUpStateFlow = signUpState.asStateFlow()

    fun signup(email: String, password: String, confirmPassword: String) {
        // Reset state to Normal
        signUpState.value = SignUpState.Normal

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpState.value = SignUpState.Error("Invalid email format")
            return
        }

        // Validate password length
        if (password.length < 6) {
            signUpState.value = SignUpState.Error("Password must be at least 6 characters")
            return
        }

        // Validate confirm password match
        if (password != confirmPassword) {
            signUpState.value = SignUpState.Error("Passwords do not match")
            return
        }

        // Proceed with Firebase Authentication
        val auth = FirebaseAuth.getInstance()
        signUpState.value = SignUpState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signUpState.value = SignUpState.Success
                } else {
                    signUpState.value = SignUpState.Error(task.exception?.localizedMessage ?: "Sign-up failed")
                }
            }
    }
}


sealed class SignUpState{
    object Loading: SignUpState()
    data class Error(val message: String): SignUpState()
    object Success: SignUpState()
    object Normal : SignUpState()

}