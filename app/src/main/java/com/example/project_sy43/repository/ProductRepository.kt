package com.example.project_sy43.repository

import com.example.project_sy43.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getItemById(id: String): Product? {
        return try {
            val document = db.collection("Post").document(id).get().await()
            document.toObject(Product::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
