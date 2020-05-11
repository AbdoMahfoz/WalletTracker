package com.abdomahfoz.wallettracker.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(
    entity = GoalEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("goalId"),
    onDelete = ForeignKey.CASCADE
)])
data class SpendEntity(
    @PrimaryKey
    override var id: String = "",
    var comment : String = "",
    var amount : Double = 0.0,
    var date : Date = Calendar.getInstance().time,
    var importance : Important = Important.AverageImportance,
    @ColumnInfo(index = true)
    var goalId : String? = null
) : BaseEntity {
    enum class Important(val value: String) {
        VeryImportant("Very Important"),
        AverageImportance("Average Importance"),
        NotImportant("Not Important")
    }
}