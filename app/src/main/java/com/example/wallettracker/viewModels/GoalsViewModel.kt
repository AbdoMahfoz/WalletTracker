package com.example.wallettracker.viewModels

import android.app.Application
import com.example.wallettracker.entities.GoalEntity
import com.example.wallettracker.entities.SpendEntity.Important
import com.example.wallettracker.entities.SpendEntity
import com.example.wallettracker.repository.IGoalsRepository
import com.example.wallettracker.repository.ISpendRepository
import kotlinx.coroutines.*
import org.kodein.di.generic.instance
import java.util.*

class GoalsViewModel(application: Application) : KodeinCoroutineViewModel(application) {
    private val goalRepo by instance<IGoalsRepository>()
    private val spendRepo by instance<ISpendRepository>()

    val goals = goalRepo.getAll()
    private fun ensureHoursZero(date : Date) : Date {
        val c = Calendar.getInstance()
        c.time = date
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.time
    }
    fun insert(goal : GoalEntity){
        uiScope.launch {
            withContext(Dispatchers.IO){
                goal.start = ensureHoursZero(goal.start)
                goal.end = ensureHoursZero(goal.end)
                goal.dailyAmount = goal.amount / (((goal.end.time - goal.start.time) / 86400000) + 1)
                goal.achieved = goal.dailyAmount
                val goalId = goalRepo.insert(goal)
                val x = (Calendar.getInstance().time.time - goal.start.time) / 86400000
                if (x == 0L) {
                    spendRepo.insert(
                        SpendEntity(
                            comment = goal.name,
                            amount = goal.dailyAmount,
                            date = goal.start,
                            importance = Important.VeryImportant,
                            goalId = goalId
                        )
                    )
                }
            }
        }
    }
}