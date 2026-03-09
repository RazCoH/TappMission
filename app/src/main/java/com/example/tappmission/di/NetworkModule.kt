package com.example.tappmission.di

import com.example.tappmission.BuildConfig
import com.example.tappmission.data.Networking
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

val networkModule = module {

    single {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            decodeEnumsCaseInsensitive = true
            coerceInputValues = true
        }
    }

    single { Networking(get(), get()) }
}
