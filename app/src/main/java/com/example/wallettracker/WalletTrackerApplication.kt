@file:Suppress("unused")

package com.example.wallettracker

import androidx.multidex.MultiDexApplication
import com.example.wallettracker.repository.IGoalsRepository
import com.example.wallettracker.repository.ISpendRepository
import com.example.wallettracker.repository.implementations.*
import com.example.wallettracker.worker.GoalsWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class WalletTrackerApplication : MultiDexApplication(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        /*
        bind<ISpendRepository>() with singleton { SpendRepositoryDb(applicationContext) }
        bind<IGoalsRepository>() with singleton { GoalsRepositoryDb(applicationContext) }
        */
        bind<ISpendRepository>() with singleton { FireBaseSpendRepository() }
        bind<IGoalsRepository>() with singleton { FireBaseGoalsRepository() }
    }
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            GoalsWorker.setUp(applicationContext)
        }
    }
}