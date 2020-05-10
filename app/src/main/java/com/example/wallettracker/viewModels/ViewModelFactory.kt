package com.example.wallettracker.viewModels

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

open class KodeinCoroutineViewModel(application : Application) : AndroidViewModel(application), KodeinAware{
    override val kodein : Kodein by closestKodein()
    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(c: Class<T>): T {
        return when {
            c.isAssignableFrom(GoalsViewModel::class.java) -> GoalsViewModel(application) as T
            c.isAssignableFrom(SpendViewModel::class.java) -> SpendViewModel(application) as T
            c.isAssignableFrom(ChartsViewModel::class.java) -> ChartsViewModel(application) as T
            c.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(application) as T
            c.isAssignableFrom(AccountViewModel::class.java) -> AccountViewModel(application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    companion object {
        @Volatile
        private lateinit var instance : ViewModelFactory
        private fun get(application: Application) : ViewModelFactory {
            synchronized(this){
                if(!::instance.isInitialized){
                    instance = ViewModelFactory(application)
                }
                return instance
            }
        }
        fun of(fragment : Fragment) : ViewModelProvider{
            val factory = get(requireNotNull(fragment.activity).application)
            return ViewModelProvider(fragment, factory)
        }
        fun of(activity: AppCompatActivity) : ViewModelProvider{
            val factory = get(activity.application)
            return ViewModelProvider(activity, factory)
        }
    }
}