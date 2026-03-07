package com.example.tappmission.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappmission.data.models.NetworkResult
import com.example.tappmission.data.repositories.WidgetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WidgetViewModel(private val repository: WidgetsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WidgetUiState>(WidgetUiState.Loading)
    val uiState: StateFlow<WidgetUiState> = _uiState.asStateFlow()

    init {
        fetchWidgetData()
    }

    fun fetchWidgetData() {
        viewModelScope.launch {
            val state = when (val result = repository.getWheelWidgetData()) {
                is NetworkResult.Success -> WidgetUiState.Success(result.data)
                is NetworkResult.Error -> WidgetUiState.Error("Error ${result.code}: ${result.msg}")
                is NetworkResult.Exception -> WidgetUiState.Error(
                    result.e.message ?: "Unknown error"
                )
            }
            _uiState.update { state }
        }
    }
}
