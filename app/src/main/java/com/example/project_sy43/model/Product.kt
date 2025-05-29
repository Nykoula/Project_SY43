package com.example.project_sy43.model

data class Product (
    val id: String = "",
    val userId: String = "", // équivalent de foreign key
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val type: String = "",
    val price: Double = 0.0,
    val taille: String = "",
    val state: String = "",
    val colis: String = "",
    val couleur: List<String> = emptyList(),
    val matieres: List<String> = emptyList(),
    val marque: String = "",
    val isFavorite: Boolean = false,
    val available: Boolean = true,
    val dateCreation: String = "",
    val photos: List<String> = emptyList()
)
