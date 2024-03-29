package com.abdomahfoz.wallettracker.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.repository.ISpendRepository
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FBSpend : FB(), ISpendRepository {
    private val spendEntities = MutableLiveData<List<SpendEntity>>()
    override fun clearLiveData() {
        spendEntities.value = listOf()
    }
    override fun setListener() : ListenerRegistration? {
        return db.collection("spends").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener{ snapshot, e ->
                if(e == null && snapshot != null){
                    uiScope.launch {
                        val items = mutableListOf<SpendEntity>()
                        withContext(Dispatchers.IO) {
                            for (item in snapshot) {
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
    override fun update(spend: SpendEntity) {
        runBlocking {
            db.collection("spends").document(spend.id).set(spend).await()
        }
    }
}