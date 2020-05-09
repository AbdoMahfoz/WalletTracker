package com.example.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wallettracker.logic.interfaces.IAuth
import com.example.wallettracker.logic.interfaces.RegisterResult
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class LoginViewModel(application: Application) : KodeinCoroutineViewModel(application) {
    private val auth by instance<IAuth>()
    private val _authComplete = MutableLiveData<Boolean>()
    private val _loading = MutableLiveData<Boolean>()
    private val _isLoginErred = MutableLiveData<Boolean>()
    private val _registerResult = MutableLiveData<RegisterResult>()

    val loading : LiveData<Boolean> get() = _loading
    val isLoginErred : LiveData<Boolean> get() = _isLoginErred
    val registerResult : LiveData<RegisterResult> get() = _registerResult

    val authComplete : LiveData<Boolean> get() = _authComplete
    fun authCompleteHandled() { _authComplete.value = null }

    init {
        if(auth.isLoggedIn()) {
            _authComplete.value = true
        }
    }
    fun logIn(email: String, password: String) {
        uiScope.launch {
            _isLoginErred.value = false
            _registerResult.value = RegisterResult.Ok
            _loading.value = true
            val loggedIn = auth.logIn(email, password)
            _loading.value = false
            if(loggedIn) {
                _authComplete.value = true
            } else {
                _isLoginErred.value = true
            }
        }
    }
    fun register(email: String, password: String){
        uiScope.launch {
            _isLoginErred.value = false
            _registerResult.value = RegisterResult.Ok
            _loading.value = true
            val registerResult = auth.register(email, password)
            _loading.value = false
            when (registerResult) {
                RegisterResult.Ok -> _authComplete.value = true
                else -> _registerResult.value = registerResult
            }
        }
    }
}