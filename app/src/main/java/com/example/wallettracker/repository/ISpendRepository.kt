package com.example.wallettracker.repository

import androidx.lifecycle.LiveData
import com.example.wallettracker.entities.SpendEntity

interface ISpendRepository{
    fun getHistory(): LiveData<List<SpendEntity>>
    fun insert(spend: SpendEntity)
}