package com.abdomahfoz.wallettracker.repository

import androidx.lifecycle.LiveData
import com.abdomahfoz.wallettracker.entities.GoalEntity
import com.abdomahfoz.wallettracker.entities.SpendEntity

interface IGoalsRepository{
    fun getAll(): LiveData<List<GoalEntity>>
    fun getAllSync(): List<GoalEntity>
    fun getLastSpendOfGoal(id: String): SpendEntity?
    fun insert(goal: GoalEntity) : String
    fun update(goal: GoalEntity)
}