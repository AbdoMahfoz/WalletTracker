package com.example.wallettracker.logic.interfaces

enum class RegisterResult { Ok, WeakPassword, InvalidEmail, EmailExists }

interface IAuth {
    fun isLoggedIn() : Boolean
    suspend fun logIn(email: String, password: String) : Boolean
    suspend fun register(email: String, password: String) : RegisterResult
    fun logOut()
}