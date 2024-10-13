package com.example.letsconnect.screens.components

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUtils @Inject constructor(
    private val auth: FirebaseAuth,
    val firestore: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    fun storeUserInfo(onComplete: (Result) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = User(email = currentUser.email ?: "", uid = currentUser.uid)
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user)
                .addOnSuccessListener {
                    onComplete(Result.Success)
                }
                .addOnFailureListener { exception ->
                    onComplete(Result.Failure(exception))
                    // Log error
                    Log.e("FirebaseUtils", "Error storing user info", exception)
                }
        } else {
            onComplete(Result.Failure(Exception("User is not authenticated")))
        }
    }

    sealed class Result {
        object Success : Result()
        class Failure(val exception: Exception) : Result()
    }
}

data class  User(val email: String, val uid: String)
