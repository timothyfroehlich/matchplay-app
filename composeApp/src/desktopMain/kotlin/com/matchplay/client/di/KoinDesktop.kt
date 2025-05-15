package com.matchplay.client.di

import com.matchplay.client.auth.ApiKeyStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { ApiKeyStorage() }
}
