package com.example.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import com.example.wallettracker.database.Important
import com.example.wallettracker.database.SpendEntity
import com.example.wallettracker.repository.ISpendRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import java.util.*

class ChartsViewModel(application: Application) : KodeinCoroutineViewModel(application) {
    private val spendRepo by instance<ISpendRepository>()
    private val allYear = spendRepo.getHistory()
    val lastMonthData = MediatorLiveData<List<Pair<String, Double>>>()
    val allYearData = MediatorLiveData<List<Pair<String, Double>>>()
    init {
        lastMonthData.addSource(allYear) { all ->
            uiScope.launch {
                var finalRes : List<Pair<String, Double>>? = null
                withContext(Dispatchers.Default) {
                    if (all.count() > 0) {
                        val c1 = Calendar.getInstance()
                        val c2 = Calendar.getInstance()
                        c1.time = all[0].date
                        finalRes = digest(all.filter { e ->
                            c2.time = e.date
                            return@filter c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH)
                        })
                    } else {
                        finalRes = digest(listOf())
                    }
                }
                lastMonthData.value = finalRes
            }
        }
        allYearData.addSource(allYear) {
            uiScope.launch {
                var finalRes : List<Pair<String, Double>>? = null
                withContext(Dispatchers.Default) {
                    finalRes = digest(it)
                }
                allYearData.value = finalRes
            }
        }
    }
    private fun digest(entities: List<SpendEntity>): List<Pair<String, Double>> {
        val resDic = mutableMapOf<String, Double>()
        for (e in Important.values()) {
            resDic[e.value] = 0.0
        }
        for (e in entities) {
            resDic[e.importance.value] = resDic[e.importance.value]!! + e.amount
        }
        return resDic.toList()
    }
}