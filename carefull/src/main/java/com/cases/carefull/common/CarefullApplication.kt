package com.cases.carefull.common

import android.app.Application
import com.cases.carefull.di.AppContainer
import com.cases.carefull.di.DefaultAppContainer

class CarefullApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}