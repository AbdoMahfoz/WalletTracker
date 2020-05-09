package com.example.wallettracker.logic.implementations

import com.example.wallettracker.logic.interfaces.IAuth
import com.example.wallettracker.logic.interfaces.RegisterResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuth : IAuth {
    private val auth = Firebase.auth

    override fun isLoggedIn(): Boolean = auth.currentUser != null
    override fun logOut() = auth.signOut()

    override suspend fun logIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e : FirebaseAuthInvalidUserException) {
            false
        } catch (e : FirebaseAuthInvalidCredentialsException) {
            false
        }
    }
    override suspend fun register(email: String, password: String): RegisterResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            RegisterResult.Ok
        } catch (e : FirebaseAuthWeakPasswordException) {
            RegisterResult.WeakPassword
        } catch (e : FirebaseAuthInvalidCredentialsException) {
            RegisterResult.InvalidEmail
        } catch (e : FirebaseAuthUserCollisionException) {
            RegisterResult.EmailExists
        }

    }
}