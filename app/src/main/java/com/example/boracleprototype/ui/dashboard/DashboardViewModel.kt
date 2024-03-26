package com.example.boracleprototype.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "App Dashboard View Text <TEMP>"
    }
    val text: LiveData<String> = _text
}