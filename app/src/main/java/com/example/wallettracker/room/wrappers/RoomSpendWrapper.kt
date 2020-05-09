package com.example.wallettracker.room.wrappers

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.wallettracker.room.WalletDatabase
import com.example.wallettracker.entities.SpendEntity
import com.example.wallettracker.repository.ISpendRepository
import java.util.*

class RoomSpendWrapper(context: Context) : ISpendRepository {
    private val db = WalletDatabase.get(context).spendRepo

    override fun getHistory(): LiveData<List<SpendEntity>> = db.getHistory()

    override fun insert(spend: SpendEntity) {
        spend.id = UUID.randomUUID().toString()
        db.insert(spend)
    }
}