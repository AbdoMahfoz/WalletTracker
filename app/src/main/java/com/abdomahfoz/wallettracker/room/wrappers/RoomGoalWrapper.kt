package com.abdomahfoz.wallettracker.room.wrappers

import android.content.Context
import androidx.lifecycle.LiveData
import com.abdomahfoz.wallettracker.room.WalletDatabase
import com.abdomahfoz.wallettracker.entities.GoalEntity
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.repository.IGoalsRepository
import java.util.*

class RoomGoalWrapper(context: Context) : IGoalsRepository {
    private val db = WalletDatabase.get(context).goalRepo

    override fun getAll(): LiveData<List<GoalEntity>> = db.getAll()
    override fun getAllSync(): List<GoalEntity> = db.getAllSync()
    override fun getLastSpendOfGoal(id: String): SpendEntity? = db.getLastSpendOfGoal(id)
    override fun update(goal: GoalEntity) = db.update(goal)

    override fun insert(goal: GoalEntity): String {
        goal.id = UUID.randomUUID().toString()
        db.insert(goal)
        return goal.id
    }
}