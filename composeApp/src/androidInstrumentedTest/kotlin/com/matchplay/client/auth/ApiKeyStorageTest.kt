package com.matchplay.client.auth

import android.app.Application
import androidx.test.core.app.ApplicationProvider

actual fun getTestApiKeyStorage(): ApiKeyStorage {
    val context = ApplicationProvider.getApplicationContext<Application>()
    return ApiKeyStorage(context)
}
