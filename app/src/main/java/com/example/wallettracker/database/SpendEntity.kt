package com.example.wallettracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wallettracker.utils.BaseEntity
import java.util.*

enum class Important(val value : String) {
    VeryImportant("Very Important"),
    AverageImportance("Average Importance"),
    NotImportant("Not Important")
}

@Entity(foreignKeys = [ForeignKey(entity = GoalEntity::class,
                                  parentColumns = arrayOf("id"),
                                  childColumns = arrayOf("goalId"),
                                  onDelete = ForeignKey.CASCADE )])
data class SpendEntity(
    @PrimaryKey
    override var id: String = "",
    var comment : String = "",
    var amount : Double = 0.0,
    var date : Date = Calendar.getInstance().time,
    var importance : Important = Important.AverageImportance,
    @ColumnInfo(index = true)
    var goalId : String? = null
) : BaseEntity

@Dao
interface SpendRepo{
    @Insert
    fun insert(item : SpendEntity)
    @Query("SELECT * FROM SpendEntity ORDER BY date DESC")
    fun getHistory() : LiveData<List<SpendEntity>>
}