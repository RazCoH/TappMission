package com.example.tappmission.di

import com.example.tappmission.data.repositories.WidgetsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WidgetsRepository(get()) }
}
