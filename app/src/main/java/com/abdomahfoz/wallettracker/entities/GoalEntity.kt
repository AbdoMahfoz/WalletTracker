package com.abdomahfoz.wallettracker.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
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