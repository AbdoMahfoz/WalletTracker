package com.abdomahfoz.wallettracker.repository

import androidx.lifecycle.LiveData
import com.abdomahfoz.wallettracker.entities.SpendEntity

interface ISpendRepository{
    fun getHistory(): LiveData<List<SpendEntity>>
    fun insert(spend: SpendEntity)
}