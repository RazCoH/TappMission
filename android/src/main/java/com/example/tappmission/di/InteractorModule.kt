package com.example.tappmission.di

import com.example.tappmission.domain.WheelWidgetInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {
    single { WheelWidgetInteractor(androidContext(), get()) }
}
