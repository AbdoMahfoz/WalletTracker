package com.example.wallettracker.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wallettracker.entities.GoalEntity
import com.example.wallettracker.entities.SpendEntity

@Dao
interface GoalDao{
    @Insert
    fun insert(goal : GoalEntity)
    @Update
    fun update(goal : GoalEntity)
    @Query("SELECT * FROM GoalEntity")
    fun getAll() : LiveData<List<GoalEntity>>
    @Query("SELECT * FROM GoalEntity")
    fun getAllSync() : List<GoalEntity>
    @Query("SELECT * FROM SpendEntity WHERE goalId = :goalId ORDER BY date DESC LIMIT 1")
    fun getLastSpendOfGoal(goalId : String) : SpendEntity?
}