package com.example.tappmission.di

import com.example.tappmission.data.local.LocalDataSource
import com.example.tappmission.data.remote.RemoteDataSource
import com.example.tappmission.data.repositories.WidgetsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { LocalDataSource(androidContext(), get()) }
    single { RemoteDataSource(get()) }
    single { WidgetsRepository(get(), get()) }
}
