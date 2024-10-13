package com.example.letsconnect.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letsconnect.screens.components.FirebaseUtils
import com.example.letsconnect.screens.components.User
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val firebaseUtils: FirebaseUtils
) : ViewModel() {

    private val _loggedInUsers = MutableStateFlow<List<User>>(emptyList())
    val loggedInUsers: StateFlow<List<User>> = _loggedInUsers

    init {
        firebaseUtils.storeUserInfo { result ->
            when (result) {
                FirebaseUtils.Result.Success -> {
                    fetchLoggedInUsers()
                }

                is FirebaseUtils.Result.Failure -> {
                    // Handle failure
                    Log.e("FirebaseUtils", "Error storing user info", result.exception)
                }
            }
        }
    }

    private fun fetchLoggedInUsers() {
        viewModelScope.launch {
            firebaseUtils.firestore.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    val users = result.documents.mapNotNull { document ->
                        val email = document.getString("email")
                        val uid = document.getString("uid")
                        if (email != null && uid != null) {
                            User(email, uid)
                        } else {
                            null
                        }
                    }
                    _loggedInUsers.value = users
                }
                .addOnFailureListener { exception ->
                    // Handle the error (e.g., log or emit an error state)
                    Log.e("MainScreenViewModel", "Error fetching logged-in users", exception)
                }
        }
    }

    fun initiateCall(email: String, isVideoCall: Boolean) {
        if (email.isNotEmpty()) {
            // Implement the call initiation logic
            if (isVideoCall){
                mutableListOf(
                    ZegoUIKitUser(
                        email
                    )
                )
            } else {
                // Voice call logic
                mutableListOf(
                    ZegoUIKitUser(
                        email
                    )
                )
            }
        }
    }
}
