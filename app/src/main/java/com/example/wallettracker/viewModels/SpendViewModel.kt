package com.example.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.wallettracker.entities.SpendEntity
import com.example.wallettracker.repository.ISpendRepository
import com.example.wallettracker.ui.spend.SpendHistoryAdapterUtil
import kotlinx.coroutines.*
import org.kodein.di.generic.instance
import java.util.*

class SpendViewModel(application: Application) : KodeinCoroutineViewModel(application) {
    private val repo by instance<ISpendRepository>()
    private val spendData = repo.getHistory()
    private val spendDataMediator = MediatorLiveData<List<SpendHistoryAdapterUtil.SpendHistoryData>>()
    val spendHistory: LiveData<List<SpendHistoryAdapterUtil.SpendHistoryData>> = spendDataMediator

    init {
        spendDataMediator.addSource(spendData) {
            if(it.count() == 0) {
                spendDataMediator.value = listOf()
                return@addSource
            }
            uiScope.launch {
                val res = mutableListOf<SpendHistoryAdapterUtil.SpendHistoryData>()
                withContext(Dispatchers.Default) {
                    val lastDate = Calendar.getInstance().apply { time = it.first().date }
                    val curDate = Calendar.getInstance()
                    var curSum = 0.0
                    var lastHeaderIndex = 0
                    res.add(
                        SpendHistoryAdapterUtil.SpendHistoryData.HeaderItem(
                            it.first().date,
                            it.first().id
                        )
                    )
                    for (item in it) {
                        curDate.time = item.date
                        if ((curDate.get(Calendar.DAY_OF_MONTH) != lastDate.get(Calendar.DAY_OF_MONTH)) ||
                            (curDate.get(Calendar.MONTH) != lastDate.get(Calendar.MONTH)) ||
                            (curDate.get(Calendar.YEAR) != lastDate.get(Calendar.YEAR))) {
                            lastDate.time = item.date
                            (res[lastHeaderIndex] as SpendHistoryAdapterUtil.SpendHistoryData.HeaderItem).sum = curSum
                            curSum = 0.0
                            lastHeaderIndex = res.count()
                            res.add(
                                SpendHistoryAdapterUtil.SpendHistoryData.HeaderItem(
                                    item.date,
                                    item.id
                                )
                            )
                        }
                        curSum += item.amount
                        res.add(SpendHistoryAdapterUtil.SpendHistoryData.SpendEntityItem(item))
                    }
                    (res[lastHeaderIndex] as SpendHistoryAdapterUtil.SpendHistoryData.HeaderItem).sum = curSum
                }
                spendDataMediator.value = res
            }
        }
    }

    fun insertNewSpend(spend: SpendEntity) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                repo.insert(spend)
            }
        }
    }
}