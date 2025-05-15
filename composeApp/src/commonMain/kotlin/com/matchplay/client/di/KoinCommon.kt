package com.matchplay.client.di

import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

fun commonModule() = module {
    // Common dependencies here (e.g., ViewModels, Repositories)
}

// In androidMain
// actual fun platformModule(): Module = module {
//     single { ApiKeyStorage(get()) } // 'get()' will resolve Context for Android
// }

// In desktopMain
// actual fun platformModule(): Module = module {
//     single { ApiKeyStorage() }
// }
