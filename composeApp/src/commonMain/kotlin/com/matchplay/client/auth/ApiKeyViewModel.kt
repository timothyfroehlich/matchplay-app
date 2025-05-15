package com.matchplay.client.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ApiKeyViewModel : ViewModel(), KoinComponent {
    private val apiKeyStorage: ApiKeyStorage by inject()
    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    init {
        CoroutineScope(Dispatchers.Main).launch {
            _apiKey.value = apiKeyStorage.getApiKey() ?: ""
        }
    }

    fun onApiKeyChanged(newKey: String) {
        _apiKey.value = newKey
    }

    fun saveApiKey() {
        val key = _apiKey.value
        CoroutineScope(Dispatchers.Main).launch {
            apiKeyStorage.saveApiKey(key)
        }
    }
}
