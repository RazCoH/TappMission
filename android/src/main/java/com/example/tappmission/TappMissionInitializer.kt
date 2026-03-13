package com.example.tappmission

import android.content.Context
import androidx.startup.Initializer
import com.example.tappmission.di.interactorModule
import com.example.tappmission.di.networkModule
import com.example.tappmission.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class TappMissionInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val modules = listOf(networkModule, repositoryModule, interactorModule)

        if (GlobalContext.getOrNull() != null) {
            // Host app already started Koin — just load our modules into it
            loadKoinModules(modules)
        } else {
            // No Koin running yet — start it ourselves
            startKoin {
                androidContext(context)
                modules(modules)
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
