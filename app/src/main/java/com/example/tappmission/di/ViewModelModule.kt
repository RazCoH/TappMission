package com.example.tappmission.di

import com.example.tappmission.presentation.viewmodels.WidgetViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { WidgetViewModel(get()) }
}
