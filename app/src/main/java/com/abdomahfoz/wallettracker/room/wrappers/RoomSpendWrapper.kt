package com.abdomahfoz.wallettracker.room.wrappers

import android.content.Context
import androidx.lifecycle.LiveData
import com.abdomahfoz.wallettracker.room.WalletDatabase
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.repository.ISpendRepository
import java.util.*

class RoomSpendWrapper(context: Context) : ISpendRepository {
    private val db = WalletDatabase.get(context).spendRepo

    override fun getHistory(): LiveData<List<SpendEntity>> = db.getHistory()

    override fun insert(spend: SpendEntity) {
        spend.id = UUID.randomUUID().toString()
        db.insert(spend)
    }

    override fun update(spend: SpendEntity) {
        db.update(spend)
    }
}