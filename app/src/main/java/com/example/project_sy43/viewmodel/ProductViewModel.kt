package com.example.project_sy43.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.project_sy43.model.Product

class ProductViewModel : ViewModel() {

    var products = mutableStateListOf<Product>()
        private set

    fun addProduct(product: Product) {
        products.add(product)
    }

    fun toggleFavorite(index: Int) {
        val product = products[index]
        products[index] = product.copy(isFavorite = !product.isFavorite)
    }

    fun removeProduct(index: Int) {
        products.removeAt(index)
    }
}