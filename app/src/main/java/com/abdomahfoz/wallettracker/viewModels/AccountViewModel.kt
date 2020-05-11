package com.abdomahfoz.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abdomahfoz.wallettracker.logic.interfaces.IAuth
import com.abdomahfoz.wallettracker.logic.interfaces.UpdateEmailResult
import com.abdomahfoz.wallettracker.logic.interfaces.UpdatePasswordResult
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class AccountViewModel(application: Application) : KodeinCoroutineViewModel(application) {
    private val auth by instance<IAuth>()

    private val _errorEmail = MutableLiveData<UpdateEmailResult>()
    private val _errorPassword = MutableLiveData<UpdatePasswordResult>()
    private val _editSuccess = MutableLiveData<Boolean>()
    private val _navigateToLoginFail = MutableLiveData<Boolean>()
    private val _navigateBack = MutableLiveData<Boolean>()
    private val _loading = MutableLiveData<Boolean>()
    val email get() = auth.getEmail()
    companion object{
        private var pendingPassword: String? = null
        private var pendingEmail: String? = null
    }

    val errorEmail: LiveData<UpdateEmailResult> get() = _errorEmail
    val errorPassword: LiveData<UpdatePasswordResult> get() = _errorPassword
    val loading: LiveData<Boolean> get() = _loading

    val editSuccess: LiveData<Boolean> get() = _editSuccess
    fun editSuccessComplete() { _editSuccess.value = null }

    val navigateToLoginFail: LiveData<Boolean> get() = _navigateToLoginFail
    fun navigateToLoginFailComplete() { _navigateToLoginFail.value = null }

    val navigateBack: LiveData<Boolean> get() = _navigateBack
    fun navigateBackComplete() { _navigateBack.value = null }

    fun handleOnStart() = uiScope.launch {
        if(pendingEmail != null || pendingPassword != null) {
            changeCredentials(pendingEmail, pendingPassword).join()
            pendingEmail = null
            pendingPassword = null
        }
    }
    fun changeCredentials(newEmail: String?, newPassword: String?) = uiScope.launch {
        var somethingHappened = false
        _errorEmail.value = UpdateEmailResult.Ok
        _errorPassword.value = UpdatePasswordResult.Ok
        _loading.value = true
        if(newEmail != null && newEmail.isNotEmpty()){
            somethingHappened = true
            val emailResult = auth.updateEmail(newEmail)
            if(emailResult != UpdateEmailResult.Ok) {
                if(emailResult == UpdateEmailResult.ReLoginRequired || emailResult == UpdateEmailResult.AccountDisabled) {
                    _navigateToLoginFail.value = true
                    pendingEmail = newEmail
                    pendingPassword = newPassword
                    return@launch
                }
                _loading.value = false
                _errorEmail.value = emailResult
                return@launch
            }
        }
        if(newPassword != null && newPassword.isNotEmpty()){
            somethingHappened = true
            val passwordResult = auth.updatePassword(newPassword)
            if(passwordResult != UpdatePasswordResult.Ok) {
                if(passwordResult == UpdatePasswordResult.ReLoginRequired || passwordResult == UpdatePasswordResult.AccountDisabled){
                    _navigateToLoginFail.value = true
                    pendingEmail = newEmail
                    pendingPassword = newPassword
                    return@launch
                }
                _loading.value = false
                _errorPassword.value = passwordResult
                return@launch
            }
        }
        if(somethingHappened) {
            _editSuccess.value = true
        } else {
            _navigateBack.value = true
        }
    }
}