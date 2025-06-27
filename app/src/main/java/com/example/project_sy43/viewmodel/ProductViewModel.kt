package com.example.project_sy43.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.project_sy43.model.Product

class ProductViewModel : ViewModel() {

    var products = mutableStateListOf<Product>()
        private set
}