package com.example.wallettracker.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class FB {
    protected val db = Firebase.firestore
    private val job = Job()
    protected val uiScope =
        CoroutineScope(Dispatchers.Main + job)
}