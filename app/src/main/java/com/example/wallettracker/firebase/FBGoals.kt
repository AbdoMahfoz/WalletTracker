package com.example.wallettracker.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wallettracker.entities.GoalEntity
import com.example.wallettracker.entities.SpendEntity
import com.example.wallettracker.repository.IGoalsRepository
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FBGoals : FB(), IGoalsRepository {
    private val goalEntities = MutableLiveData<List<GoalEntity>>()
    init {
        db.collection("goals").addSnapshotListener{ snapshot, e ->
            if(e == null && snapshot != null){
                uiScope.launch {
                    val items = mutableListOf<GoalEntity>()
                    withContext(Dispatchers.IO){
                        for(item in snapshot){
                            items.add(item.toObject())
                        }
                    }
                    goalEntities.value = items
                }
            }
        }
    }
    override fun getAll(): LiveData<List<GoalEntity>> = goalEntities
    override fun getAllSync(): List<GoalEntity> {
        var res : List<GoalEntity>? = null
        runBlocking {
            res = db.collection("goals").get().await().toObjects()
        }
        return res!!
    }
    override fun getLastSpendOfGoal(id: String): SpendEntity? {
        var res : SpendEntity? = null
        runBlocking {
            val q = db.collection("spends").whereEqualTo("goalId", id).get().await()
            if(q.isEmpty) {
                res = null
            }
            res = q.first().toObject()
        }
        return res
    }
    override fun insert(goal: GoalEntity): String {
        val doc = db.collection("goals").document()
        goal.id = doc.id
        runBlocking {
            doc.set(goal).await()
        }
        return goal.id
    }
    override fun update(goal: GoalEntity) {
        runBlocking {
            db.collection("goals").document(goal.id).set(goal).await()
        }
    }
}