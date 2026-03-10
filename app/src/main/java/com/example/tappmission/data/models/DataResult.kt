package com.example.tappmission.data.models

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val code: Int, val msg: String) : DataResult<Nothing>()
    data class Exception(val e: Throwable) : DataResult<Nothing>()
}
