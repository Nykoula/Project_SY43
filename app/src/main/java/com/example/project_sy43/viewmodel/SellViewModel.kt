package com.example.project_sy43.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.project_sy43.repository.ProductRepository
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf

class SellViewModel : ViewModel(){

    private val repository = ProductRepository()

    var idUser = mutableStateOf("")
    var buyer = mutableStateOf("")
    var productId = mutableStateOf("")
    var productTitle = mutableStateOf("")
    var productDescription = mutableStateOf("")
    var productPrice = mutableStateOf("")
    var selectedCategory = mutableStateOf("")
    var selectedType = mutableStateOf("")
    var selectedState = mutableStateOf("")
    var selectedColors = mutableStateOf(emptySet<String>())
    var selectedMaterial = mutableStateOf(emptySet<String>())
    var selectedSize = mutableStateOf("")
    var selectedColis = mutableStateOf("")
    var dateCreation = mutableStateOf("")

    // URI locales pour l'affichage des images sélectionnées
    var productPhotoUris = mutableStateOf<List<Uri>>(emptyList())
        private set

    // URLs Firebase pour la base de données
    var productPhotoUrls = mutableStateOf<List<String>>(emptyList())
        private set

    var searchResults = mutableStateListOf<SellViewModel>()
    var isAvailable = mutableStateOf(true)

    fun setBuyer(buyer: String) {
        this.buyer.value = buyer
    }

    fun getBuyer(): String {
        return buyer.value
    }

    fun setSearchResults(results: List<SellViewModel>) {
        searchResults.clear()
        searchResults.addAll(results)
    }

    fun setProductTitle(title: String) {
        productTitle.value = title
    }

    fun setProductDescription(description: String) {
        productDescription.value = description
    }

    fun setProductPrice(price: String) {
        productPrice.value = price
    }

    fun setProductCategory(category: String) {
        selectedCategory.value = category
    }

    fun setProductType(type: String) {
        selectedType.value = type
    }

    fun setProductState(state: String) {
        selectedState.value = state
    }

    fun setSelectedColors(colors: Set<String>) {
        selectedColors.value = colors
    }

    fun setSelectedMaterial(material: Set<String>) {
        selectedMaterial.value = material
    }

    fun setSelectedSize(size: String) {
        selectedSize.value = size
    }

    fun setProductColis(colis: String) {
        selectedColis.value = colis
    }

    fun setProductAvailability(available: Boolean) {
        isAvailable.value = available
    }

    fun setdateCreation(date: String) {
        dateCreation.value = date
    }

    // Ajouter une URI locale (pour l'affichage)
    fun addProductPhotoUri(uri: Uri) {
        val currentList = productPhotoUris.value.toMutableList()
        if (!currentList.contains(uri)) {
            currentList.add(uri)
            productPhotoUris.value = currentList
        }
    }

    // Supprimer une URI locale
    fun removeProductPhotoUri(uri: Uri) {
        val currentList = productPhotoUris.value.toMutableList()
        currentList.remove(uri)
        productPhotoUris.value = currentList
    }

    // Remplacer toutes les URIs locales
    fun setProductPhotoUris(uris: List<Uri>) {
        productPhotoUris.value = uris
    }

    // Ajouter une URL Firebase (pour la DB)
    fun addProductPhotoUrl(url: String) {
        val currentList = productPhotoUrls.value.toMutableList()
        if (!currentList.contains(url)) {
            currentList.add(url)
            productPhotoUrls.value = currentList
        }
    }

    // Remplacer toutes les URLs Firebase
    fun setProductPhotoUrls(urls: List<String>) {
        productPhotoUrls.value = urls
    }

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            Log.d("SellViewModel", "loadItem called with itemId=$itemId")
            val item = repository.getItemById(itemId)
            if (item != null) {
                Log.d("SellViewModel", "Item loaded: $item")
                productId.value = itemId
                productTitle.value = item.title
                productDescription.value = item.description
                productPrice.value = item.price.toString()
                selectedCategory.value = item.category
                selectedType.value = item.type
                selectedState.value = item.state
                selectedColors.value = item.color.toSet()
                selectedMaterial.value = item.material.toSet()
                selectedSize.value = item.size
                selectedColis.value = item.colis
                isAvailable.value = item.available
                dateCreation.value = item.dateCreation

                // Charger les URLs Firebase (pour l'édition)
                productPhotoUrls.value = item.photos ?: emptyList()

                // Pour l'édition, on garde les URIs vides car on affichera les URLs
                productPhotoUris.value = emptyList()

                productPhotoUrls.value.forEachIndexed { index, url ->
                    Log.d("SellViewModel", "Photo $index: $url")
                }
            } else {
                Log.e("SellViewModel", "Item not found for ID: $itemId")
            }
        }
    }

    fun reset() {
        productId.value = ""
        productTitle.value = ""
        productDescription.value = ""
        productPrice.value = ""
        selectedCategory.value = ""
        selectedType.value = ""
        selectedState.value = ""
        selectedColors.value = emptySet()
        selectedMaterial.value = emptySet()
        selectedSize.value = ""
        selectedColis.value = ""
        isAvailable.value = true
        dateCreation.value = ""
        productPhotoUris.value = emptyList()
        productPhotoUrls.value = emptyList()
    }
}