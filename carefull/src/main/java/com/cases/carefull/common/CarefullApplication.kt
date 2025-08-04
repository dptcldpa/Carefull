package com.cases.carefull.common

import android.app.Application
import com.cases.carefull.di.AppContainer
import com.cases.carefull.di.DefaultAppContainer

const val COIL_MEMORY_CACHE_SIZE_PERCENT = 0.3

//Coil Disk Cache Size Setting
const val COIL_DISK_CACHE_DIR_NAME = "coil_file_cache"
const val COIL_DISK_CACHE_MAX_SIZE = 1024 * 1024 * 100

class CarefullApplication : Application(){

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}