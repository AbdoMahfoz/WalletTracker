package com.example.wallettracker.repository

import androidx.lifecycle.LiveData
import com.example.wallettracker.database.GoalEntity
import com.example.wallettracker.database.SpendEntity

interface ISpendRepository{
    fun getHistory(): LiveData<List<SpendEntity>>
    fun insert(spend: SpendEntity)
}
interface IGoalsRepository{
    fun getAll(): LiveData<List<GoalEntity>>
    fun getAllSync(): List<GoalEntity>
    fun getLastSpendOfGoal(id: String): SpendEntity?
    fun insert(goal: GoalEntity) : String
    fun update(goal: GoalEntity)
}