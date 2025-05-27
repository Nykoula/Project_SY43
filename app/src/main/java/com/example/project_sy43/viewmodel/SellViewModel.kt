package com.example.project_sy43.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SellViewModel : ViewModel(){
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
    var productPhotoUri by mutableStateOf<Uri?>(null)
        private set
    var isAvailable = mutableStateOf(true)

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
    fun updateProductPhotoUri(uri: Uri) {
        productPhotoUri = uri
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
        productPhotoUri = null
    }
}