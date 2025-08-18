package com.cases.carefull.common

import android.app.Application
import com.cases.carefull.di.AppContainer
import com.cases.carefull.di.DefaultAppContainer
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings

const val COIL_MEMORY_CACHE_SIZE_PERCENT = 0.3

//Coil Disk Cache Size Setting
const val COIL_DISK_CACHE_DIR_NAME = "coil_file_cache"
const val COIL_DISK_CACHE_MAX_SIZE = 1024 * 1024 * 100

class CarefullApplication : Application(){

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        KakaoSdk.init(this, "816cce424892459eff0c0988017b65f2")

//        setUpFirestoreLocalSinkCache()
    }
    
    fun setUpFirestoreLocalSinkCache() {
        val settings = firestoreSettings {
            setLocalCacheSettings(persistentCacheSettings {
                setSizeBytes(1024 * 1024 * 1024)
            })
        }
        Firebase.firestore.firestoreSettings = settings
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    }
}