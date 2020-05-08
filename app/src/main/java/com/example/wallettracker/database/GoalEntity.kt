package com.example.wallettracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wallettracker.utils.BaseEntity
import java.util.*
import kotlin.math.min

@Entity
data class GoalEntity(
    @PrimaryKey
    override var id : String = "",
    var name : String = "",
    var start : Date = Calendar.getInstance().time,
    var end : Date = Calendar.getInstance().time,
    var amount : Double = 0.0,
    var dailyAmount : Double = 0.0,
    var achieved : Double = 0.0
) : BaseEntity {
    val percent get() = min(100, ((achieved / amount) * 100).toInt())
}

@Dao
interface GoalRepo{
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