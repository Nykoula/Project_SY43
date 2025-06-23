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
    var productPhotoUri = mutableStateOf<List<Uri>>(emptyList())
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

    fun addProductPhotoUri(uri: Uri) {
        val currentList = productPhotoUri.value.toMutableList()
        if (!currentList.contains(uri)) {
            currentList.add(uri)
            productPhotoUri.value = currentList
        }
    }

    //remplace toutes les photos de la liste en une seule fois
    fun setProductPhotoUris(uris: List<Uri>) {
        productPhotoUri.value = uris
    }

    fun removeProductPhotoUri(uri: Uri) {
        val currentList = productPhotoUri.value.toMutableList()
        currentList.remove(uri)
        productPhotoUri.value = currentList
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

                // CORRECTION IMPORTANTE: Convertir les URLs en Uri
                val photoUris = item.photos.mapNotNull { photoUrl ->
                    try {
                        Uri.parse(photoUrl)
                    } catch (e: Exception) {
                        Log.e("SellViewModel", "Erreur conversion URL vers Uri: $photoUrl", e)
                        null
                    }
                }
                productPhotoUri.value = photoUris

                Log.d("SellViewModel", "Photos loaded: ${photoUris.size} photos")
                photoUris.forEachIndexed { index, uri ->
                    Log.d("SellViewModel", "Photo $index: $uri")
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
        productPhotoUri.value = emptyList()
    }
}