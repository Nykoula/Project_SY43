package com.example.project_sy43.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.project_sy43.model.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonViewModel : ViewModel() {
//    var person by mutableStateOf(Person())
//        private set
var person by mutableStateOf<Person?>(null)
    private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val db = FirebaseFirestore.getInstance()


    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                fetchPerson()
            } else {
                person = null
            }
        }
    }

    fun fetchPerson() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            errorMessage = "Utilisateur non connecté"
            person = null
            return
        }

        isLoading = true
        errorMessage = null

        db.collection("Person").document(userId).get()
            .addOnSuccessListener { document ->
                person = document.toObject(Person::class.java) ?: Person()
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = e.message
                isLoading = false
            }
    }

    fun updatePerson(updatedPerson: Person) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            errorMessage = "Utilisateur non connecté"
            return
        }


        db.collection("Person").document(userId)
            .set(updatedPerson)
            .addOnSuccessListener {
                person = updatedPerson
            }
            .addOnFailureListener { e ->
                errorMessage = e.message
            }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        person = null // Optionnel : reset les données perso dans le ViewModel
    }
}
