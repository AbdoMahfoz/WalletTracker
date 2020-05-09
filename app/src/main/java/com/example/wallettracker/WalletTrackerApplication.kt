@file:Suppress("unused")

package com.example.wallettracker

import androidx.multidex.MultiDexApplication
import com.example.wallettracker.firebase.FBGoals
import com.example.wallettracker.firebase.FBSpend
import com.example.wallettracker.logic.implementations.FirebaseAuth
import com.example.wallettracker.logic.interfaces.IAuth
import com.example.wallettracker.repository.IGoalsRepository
import com.example.wallettracker.repository.ISpendRepository
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
        /*bind<ISpendRepository>() with singleton { RoomSpendWrapper(applicationContext) }
        bind<IGoalsRepository>() with singleton { RoomGoalWrapper(applicationContext) }*/
        bind<ISpendRepository>() with singleton { FBSpend() }
        bind<IGoalsRepository>() with singleton { FBGoals() }
        bind<IAuth>() with singleton { FirebaseAuth() }
    }
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            GoalsWorker.setUp(applicationContext)
        }
    }
}