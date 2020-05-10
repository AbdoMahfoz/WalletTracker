package com.example.wallettracker.logic.implementations

import com.example.wallettracker.logic.interfaces.IAuth
import com.example.wallettracker.logic.interfaces.RegisterResult
import com.example.wallettracker.logic.interfaces.UpdateEmailResult
import com.example.wallettracker.logic.interfaces.UpdatePasswordResult
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuth : IAuth {
    private val auth = Firebase.auth

    override fun isLoggedIn(): Boolean = auth.currentUser != null
    override fun getEmail(): String = auth.currentUser!!.email!!
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
        }
        catch (e : FirebaseAuthWeakPasswordException) { RegisterResult.WeakPassword }
        catch (e : FirebaseAuthInvalidCredentialsException) { RegisterResult.InvalidEmail }
        catch (e : FirebaseAuthUserCollisionException) { RegisterResult.EmailExists }
    }
    override suspend fun reLogIn(email: String, password: String): Boolean {
        return try {
            auth.currentUser!!.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
            true
        }
        catch (e : FirebaseAuthInvalidUserException) { false }
        catch (e : FirebaseAuthInvalidCredentialsException) { false }
    }
    override suspend fun updateEmail(newEmail: String): UpdateEmailResult {
        return try {
            auth.currentUser!!.updateEmail(newEmail).await()
            UpdateEmailResult.Ok
        }
        catch (e : FirebaseAuthInvalidCredentialsException) { UpdateEmailResult.InvalidEmail }
        catch (e : FirebaseAuthUserCollisionException) { UpdateEmailResult.EmailExists }
        catch (e : FirebaseAuthInvalidUserException) { UpdateEmailResult.AccountDisabled }
        catch (e : FirebaseAuthRecentLoginRequiredException) { UpdateEmailResult.ReLoginRequired }
    }
    override suspend fun updatePassword(newPassword: String): UpdatePasswordResult {
        return try {
            auth.currentUser!!.updatePassword(newPassword).await()
            UpdatePasswordResult.Ok
        }
        catch (e : FirebaseAuthWeakPasswordException) { UpdatePasswordResult.WeakPassword }
        catch (e : FirebaseAuthInvalidUserException) { UpdatePasswordResult.AccountDisabled }
        catch (e : FirebaseAuthRecentLoginRequiredException) { UpdatePasswordResult.ReLoginRequired }
    }
}