package com.example.project_sy43.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class SharedViewModel : ViewModel() {
    var selectedType = mutableStateOf("")

    fun setType(type: String) {
        selectedType.value = type
        Log.d("FilterSearch", "Selected type set to: $type")
    }
}
