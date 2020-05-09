package com.example.wallettracker.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class FB {
    private val _db = Firebase.firestore
    protected val db : DocumentReference get() {
        return _db.collection("userData").document(auth.currentUser!!.uid)
    }
    protected val auth = Firebase.auth
    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var oldListener : ListenerRegistration? = null
    init {
        Firebase.auth.addAuthStateListener {
            oldListener?.remove()
            clearLiveData()
            if(it.currentUser != null) {
                oldListener = setListener()
            }
        }
    }
    abstract fun setListener() : ListenerRegistration?
    abstract fun clearLiveData()
}