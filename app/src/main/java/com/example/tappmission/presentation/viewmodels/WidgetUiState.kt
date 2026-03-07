package com.example.tappmission.presentation.viewmodels

import com.example.tappmission.data.responses.WidgetResponse

sealed interface WidgetUiState {
    data object Loading : WidgetUiState
    data class Success(val data: WidgetResponse) : WidgetUiState
    data class Error(val message: String) : WidgetUiState
}
