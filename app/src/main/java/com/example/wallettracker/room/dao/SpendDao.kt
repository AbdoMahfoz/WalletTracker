package com.example.wallettracker.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wallettracker.entities.SpendEntity

@Dao
interface SpendDao {
    @Insert
    fun insert(spend : SpendEntity)
    @Query("SELECT * FROM SpendEntity ORDER BY date DESC")
    fun getHistory() : LiveData<List<SpendEntity>>
}