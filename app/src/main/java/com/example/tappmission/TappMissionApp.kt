package com.example.tappmission

import android.app.Application
import com.example.tappmission.di.networkModule
import com.example.tappmission.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TappMissionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TappMissionApp)
            modules(
                networkModule,
                repositoryModule
            )
        }
    }
}
