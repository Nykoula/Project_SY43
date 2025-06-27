package com.example.project_sy43.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sy43.repository.ProductRepository
import kotlinx.coroutines.launch

class SellViewModel : ViewModel() {

    // Repository instance to access product data
    private val repository = ProductRepository()

    // Mutable states holding product and user related data
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

    // Local URIs for displaying selected images
    var productPhotoUris = mutableStateOf<List<Uri>>(emptyList())
        private set

    // Firebase URLs for storing in the database
    var productPhotoUrls = mutableStateOf<List<String>>(emptyList())
        private set

    // Mutable list holding search results of SellViewModel instances
    var searchResults = mutableStateListOf<SellViewModel>()
    var isAvailable = mutableStateOf(true)

    // Set the buyer value
    fun setBuyer(buyer: String) {
        this.buyer.value = buyer
    }

    // Get the buyer value
    fun getBuyer(): String {
        return buyer.value
    }

    // Set the search results list
    fun setSearchResults(results: List<SellViewModel>) {
        searchResults.clear()
        searchResults.addAll(results)
    }

    // Setters for product details
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

    fun setdateCreation(date: String) {
        dateCreation.value = date
    }

    // Add a local URI for product photo if not already present
    fun addProductPhotoUri(uri: Uri) {
        val currentList = productPhotoUris.value.toMutableList()
        if (!currentList.contains(uri)) {
            currentList.add(uri)
            productPhotoUris.value = currentList
        }
    }

    // Remove a local URI for product photo
    fun removeProductPhotoUri(uri: Uri) {
        val currentList = productPhotoUris.value.toMutableList()
        currentList.remove(uri)
        productPhotoUris.value = currentList
    }

    // Replace all Firebase URLs for product photos
    fun setProductPhotoUrls(urls: List<String>) {
        productPhotoUrls.value = urls
    }

    // Load product item details by ID and update states accordingly
    fun loadItem(itemId: String) {
        viewModelScope.launch {
            Log.d("SellViewModel" , "loadItem called with itemId=$itemId")
            val item = repository.getItemById(itemId)
            if (item != null) {
                Log.d("SellViewModel" , "Item loaded: $item")
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

                // Load Firebase URLs for editing
                productPhotoUrls.value = item.photos ?: emptyList()

                // For editing, keep local URIs empty as URLs will be displayed
                productPhotoUris.value = emptyList()

                productPhotoUrls.value.forEachIndexed { index , url ->
                    Log.d("SellViewModel" , "Photo $index: $url")
                }
            } else {
                Log.e("SellViewModel" , "Item not found for ID: $itemId")
            }
        }
    }

    // Reset all product-related states to default values
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