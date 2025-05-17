package com.example.project_sy43.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SellViewModel : ViewModel(){
    var productTitle = mutableStateOf("")
    var productDescription = mutableStateOf("")
    var productPrice = mutableStateOf("")
    var selectedCategory = mutableStateOf("")
    var selectedState = mutableStateOf("")
    var selectedColors = mutableStateOf<List<String>>(emptyList())
    var selectedMaterial = mutableStateOf<List<String>>(emptyList())
    var selectedSize = mutableStateOf("")
    var selectedColis = mutableStateOf("")
    var productPhotoUri by mutableStateOf<Uri?>(null)
        private set

    //var productPhotoUrl by mutableStateOf("")
        //private set


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

    fun setProductState(state: String) {
        selectedState.value = state
    }

    fun setSelectedColors(colors: List<String>) {
        selectedColors.value = colors
    }

    fun setSelectedMaterial(material: List<String>) {
        selectedMaterial.value = material
    }

    fun setSelectedSize(size: String) {
        selectedSize.value = size
    }

    fun setProductColis(colis: String) {
        selectedColis.value = colis
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
        selectedState.value = ""
        selectedColors.value = emptyList()
        selectedMaterial.value = emptyList()
        selectedSize.value = ""
        selectedColis.value = ""
        productPhotoUri = null
    }
}