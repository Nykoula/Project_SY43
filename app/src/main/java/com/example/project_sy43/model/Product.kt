package com.example.project_sy43.model

data class Product (
    val userId: String, // équivalent de foreign key
    val title: String,
    val description: String,
    val category: String,
    val type: String,
    val price: Double,
    val taille: String,
    val couleur: List<String>,
    val matieres: List<String>,
    val marque: String,
    val isFavorite: Boolean,
    val dateCreation: String
)
