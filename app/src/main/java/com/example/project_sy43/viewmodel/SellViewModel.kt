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

class SellViewModel : ViewModel(){

    private val repository = ProductRepository()

    var idUser = mutableStateOf("")
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
    //    var productPhotoUri by mutableStateOf<Uri?>(null)
//        private set
    var searchResults = mutableStateOf<List<SellViewModel>>(emptyList())
        private set
    var isAvailable = mutableStateOf(true)


    fun setSearchResults(results: List<SellViewModel>) {
        searchResults.value = results
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

    // updateProductPhotoUri
//    fun updateProductPhotoUri(uri: Uri) {
//        productPhotoUri = uri
//    }
    fun addProductPhotoUri(uri: Uri) {
        productPhotoUri.value = productPhotoUri.value + uri
    }

    //remplace toutes les photos de la liste en une seule fois
    fun setProductPhotoUris(uris: List<Uri>) {
        productPhotoUri.value = uris
    }

    //supprimer une photo de la liste
    fun removeProductPhotoUri(uri: Uri) {
        productPhotoUri.value = productPhotoUri.value.filter { it != uri }
    }



    fun loadItem(itemId: String) {
        viewModelScope.launch {
            Log.d("SellViewModel", "loadItem called with itemId=$itemId")
            val item = repository.getItemById(itemId)
            if (item != null) {
                Log.d("SellViewModel", "Item loaded: $item")
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
                productPhotoUri.value = item.photos.map { Uri.parse(it) }
                //productPhotoUri = item.photos.firstOrNull()?.let { Uri.parse(it) }
            }
        }
    }

    fun reset() {
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
        //productPhotoUri = null
        productPhotoUri.value = emptyList()
    }
}