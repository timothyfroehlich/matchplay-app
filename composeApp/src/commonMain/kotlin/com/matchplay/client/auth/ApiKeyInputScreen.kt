package com.matchplay.client.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun ApiKeyInputScreen() {
    val viewModel: ApiKeyViewModel = koinInject()
    val apiKey by viewModel.apiKey.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Text("Enter your Matchplay Premium API Key:")
        BasicTextField(
            value = apiKey,
            onValueChange = { viewModel.onApiKeyChanged(it) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.saveApiKey() }) {
            Text("Save API Key")
        }
    }
}
