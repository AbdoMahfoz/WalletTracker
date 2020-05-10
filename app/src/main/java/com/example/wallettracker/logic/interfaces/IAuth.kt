package com.example.wallettracker.logic.interfaces

enum class RegisterResult { Ok, WeakPassword, InvalidEmail, EmailExists }
enum class UpdateEmailResult { Ok, InvalidEmail, EmailExists, AccountDisabled, ReLoginRequired }
enum class UpdatePasswordResult { Ok, WeakPassword, AccountDisabled, ReLoginRequired}

interface IAuth {
    fun isLoggedIn() : Boolean
    suspend fun logIn(email: String, password: String) : Boolean
    suspend fun register(email: String, password: String) : RegisterResult
    suspend fun updateEmail(newEmail: String) : UpdateEmailResult
    suspend fun updatePassword(newPassword: String) : UpdatePasswordResult
    suspend fun reLogIn(email: String, password: String) : Boolean
    fun getEmail() : String
    fun logOut()
}