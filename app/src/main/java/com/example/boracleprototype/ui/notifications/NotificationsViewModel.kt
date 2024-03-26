package com.example.boracleprototype.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "App Notifications View Text <TEMP>"
    }
    val text: LiveData<String> = _text
}