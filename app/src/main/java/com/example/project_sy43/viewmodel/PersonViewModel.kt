package com.example.project_sy43.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.project_sy43.model.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonViewModel : ViewModel() {
    // Mutable state holding the current Person object or null if not loaded
    var person by mutableStateOf<Person?>(null)
        private set

    // Mutable state holding error messages for the UI
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Mutable state indicating if a loading operation is in progress
    var isLoading by mutableStateOf(false)
        private set

    // Firestore database instance
    private val db = FirebaseFirestore.getInstance()

    // Initialization block to listen for authentication state changes
    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Fetch person data when user is logged in
                fetchPerson()
            } else {
                // Clear person data when user logs out
                person = null
            }
        }
    }

    // Fetch the current user's Person data from Firestore
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
                // Convert Firestore document to Person object or default Person if null
                person = document.toObject(Person::class.java) ?: Person()
                isLoading = false
            }
            .addOnFailureListener { e ->
                // Set error message on failure
                errorMessage = e.message
                isLoading = false
            }
    }

    // Update the current user's Person data in Firestore
    fun updatePerson(updatedPerson: Person) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            errorMessage = "Utilisateur non connecté"
            return
        }

        db.collection("Person").document(userId)
            .set(updatedPerson)
            .addOnSuccessListener {
                // Update local state on success
                person = updatedPerson
            }
            .addOnFailureListener { e ->
                // Set error message on failure
                errorMessage = e.message
            }
    }

    // Log out the current user and clear local person data
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        person = null // Optionally reset personal data in the ViewModel
    }
}