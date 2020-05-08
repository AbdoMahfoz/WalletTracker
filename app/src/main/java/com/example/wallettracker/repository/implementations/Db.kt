package com.example.wallettracker.repository.implementations

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.wallettracker.database.GoalEntity
import com.example.wallettracker.database.SpendEntity
import com.example.wallettracker.database.WalletDatabase
import com.example.wallettracker.repository.IGoalsRepository
import com.example.wallettracker.repository.ISpendRepository
import java.util.*

sealed class DbRepository(applicationContext : Context) {
    protected val db = WalletDatabase.get(applicationContext)
}

class SpendRepositoryDb(applicationContext : Context) : DbRepository(applicationContext), ISpendRepository {
    override fun getHistory(): LiveData<List<SpendEntity>> = db.spendRepo.getHistory()
    override fun insert(spend: SpendEntity) {
        val uuid = UUID.randomUUID().toString()
        spend.id = uuid
        db.spendRepo.insert(spend)
    }
}
class GoalsRepositoryDb(applicationContext : Context) : DbRepository(applicationContext), IGoalsRepository {
    override fun getAll(): LiveData<List<GoalEntity>> = db.goalRepo.getAll()
    override fun getAllSync(): List<GoalEntity> = db.goalRepo.getAllSync()
    override fun getLastSpendOfGoal(id: String): SpendEntity? = db.goalRepo.getLastSpendOfGoal(id)
    override fun insert(goal: GoalEntity) : String {
        val uuid = UUID.randomUUID().toString()
        goal.id = uuid
        db.goalRepo.insert(goal)
        return uuid
    }
    override fun update(goal: GoalEntity) = db.goalRepo.update(goal)
}