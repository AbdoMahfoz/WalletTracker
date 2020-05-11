package com.abdomahfoz.wallettracker.entities

import com.github.abdomahfoz.genericrecycleradapter.GenericRecyclerEntity

interface BaseEntity : GenericRecyclerEntity {
    override val id : String
    override fun equals(other: Any?) : Boolean
}