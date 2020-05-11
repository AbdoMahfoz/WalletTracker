package com.abdomahfoz.wallettracker.ui.spend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.abdomahfoz.wallettracker.R
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.databinding.SpendHistoryHeaderBinding
import com.abdomahfoz.wallettracker.databinding.SpendHistoryItemBinding
import com.abdomahfoz.wallettracker.databinding.SpendHistoryItemGoalBinding
import com.abdomahfoz.wallettracker.entities.BaseEntity
import com.github.abdomahfoz.genericrecycleradapter.GenericViewHolder
import java.lang.Exception
import java.util.*

object SpendHistoryAdapterUtil{
    abstract class SpendHistoryData :
        BaseEntity {
        data class SpendEntityItem(val spendEntity: SpendEntity) : SpendHistoryData() {
            override val id: String get() = spendEntity.id
        }
        data class HeaderItem(val header: Date, override val id: String, var sum : Double = 0.0) : SpendHistoryData()
        abstract override val id : String
    }
    private fun createSpendHistoryViewHolder(parent : ViewGroup) : GenericViewHolder<SpendHistoryData> {
        val binding: SpendHistoryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.spend_history_item, parent, false
        )
        return GenericViewHolder(binding)
    }
    private fun createGoalViewHolder(parent : ViewGroup) : GenericViewHolder<SpendHistoryData>{
        val binding: SpendHistoryItemGoalBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.spend_history_item_goal, parent, false
        )
        return GenericViewHolder(binding)
    }
    private fun createHeaderViewHolder(parent : ViewGroup) : GenericViewHolder<SpendHistoryData>{
        val binding: SpendHistoryHeaderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.spend_history_header, parent, false
        )
        return GenericViewHolder(binding)
    }
    fun viewHolderFactory(container: ViewGroup, viewType: Int) : GenericViewHolder<SpendHistoryData> {
        return when(viewType){
            0 -> createSpendHistoryViewHolder(container)
            1 -> createGoalViewHolder(container)
            2 -> createHeaderViewHolder(container)
            else -> throw Exception("Unknown viewType in SpendHistoryAdapter")
        }
    }
    fun viewHolderType(item : SpendHistoryData) : Int {
        return when(item){
            is SpendHistoryData.SpendEntityItem -> if (item.spendEntity.goalId == null) 0 else 1
            is SpendHistoryData.HeaderItem -> 2
            else -> -1
        }
    }
}