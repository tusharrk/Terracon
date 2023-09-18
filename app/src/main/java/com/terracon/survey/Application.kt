package com.terracon.survey

import android.app.Application
import com.terracon.survey.di.dataSourceModule
import com.terracon.survey.di.databaseModule
import com.terracon.survey.di.netModule
import com.terracon.survey.di.repositoryModule
import com.terracon.survey.di.serviceModule
import com.terracon.survey.di.viewModelModule
import com.terracon.survey.utils.LogHelper
import com.terracon.survey.utils.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)
        startKoin{
            androidContext(this@MainApplication)
            androidLogger()
            modules(listOf(repositoryModule, viewModelModule, netModule, serviceModule, databaseModule,dataSourceModule))

        }

        //setup logs
        LogHelper.init(this)

    }
}