package com.example.wallettracker.entities

interface BaseEntity {
    val id : String
    override fun equals(other: Any?) : Boolean
}