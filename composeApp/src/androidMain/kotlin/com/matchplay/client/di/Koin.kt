package com.matchplay.client.di

import android.content.Context
import com.matchplay.client.auth.ApiKeyStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { ApiKeyStorage(get<Context>()) }
}
