package com.example.wallettracker.repository.implementations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wallettracker.database.GoalEntity
import com.example.wallettracker.database.SpendEntity
import com.example.wallettracker.repository.IGoalsRepository
import com.example.wallettracker.repository.ISpendRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

sealed class FB {
    protected val db = Firebase.firestore
    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)
}

class FireBaseSpendRepository : FB(), ISpendRepository {
    private val spendEntities = MutableLiveData<List<SpendEntity>>()
    init {
        db.collection("spends")
          .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener{ snapshot, e ->
            if(e == null && snapshot != null){
                uiScope.launch {
                    val items = mutableListOf<SpendEntity>()
                    withContext(Dispatchers.IO){
                        for(item in snapshot) {
                            items.add(item.toObject())
                        }
                    }
                    spendEntities.value = items
                }
            }
        }
    }
    override fun getHistory(): LiveData<List<SpendEntity>> = spendEntities
    override fun insert(spend: SpendEntity) {
        val doc = db.collection("spends").document()
        spend.id = doc.id
        runBlocking {
            doc.set(spend).await()
        }
    }
}

class FireBaseGoalsRepository : FB(), IGoalsRepository {
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