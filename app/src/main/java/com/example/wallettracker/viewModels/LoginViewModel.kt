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
    private var isReLogin: Boolean? = null

    val loading : LiveData<Boolean> get() = _loading
    val isLoginErred : LiveData<Boolean> get() = _isLoginErred
    val registerResult : LiveData<RegisterResult> get() = _registerResult

    val authComplete : LiveData<Boolean> get() = _authComplete
    fun authCompleteHandled() { _authComplete.value = null }

    fun handleIsReLogin(isReLogin: Boolean) {
        this.isReLogin = isReLogin
        if(!isReLogin && auth.isLoggedIn()) {
            _authComplete.value = true
        }
    }
    fun logIn(email: String, password: String) {
        uiScope.launch {
            _isLoginErred.value = false
            _registerResult.value = RegisterResult.Ok
            _loading.value = true
            val loggedIn = if(isReLogin!!) {
                auth.reLogIn(email, password)
            } else {
                auth.logIn(email, password)
            }
            if(loggedIn) {
                _authComplete.value = true
            } else {
                _loading.value = false
                _isLoginErred.value = true
            }
        }
    }
    fun register(email: String, password: String){
        uiScope.launch {
            _isLoginErred.value = false
            _registerResult.value = RegisterResult.Ok
            _loading.value = true
            when (val registerResult = auth.register(email, password)) {
                RegisterResult.Ok -> _authComplete.value = true
                else -> {
                    _loading.value = false
                    _registerResult.value = registerResult
                }
            }
        }
    }
}