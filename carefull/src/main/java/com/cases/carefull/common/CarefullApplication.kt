package com.cases.carefull.common

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.cases.carefull.BuildConfig.kakao_native_app_key
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

const val COIL_MEMORY_CACHE_SIZE_PERCENT = 0.3

//Coil Disk Cache Size Setting
const val COIL_DISK_CACHE_DIR_NAME = "coil_file_cache"
const val COIL_DISK_CACHE_MAX_SIZE = 1024 * 1024 * 100

@HiltAndroidApp
class CarefullApplication : Application() {
	
	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate() {
		super.onCreate()
//		setUpFirestoreLocalSinkCache()
		KakaoSdk.init(this, kakao_native_app_key)
	}
	
	fun setUpFirestoreLocalSinkCache() {
		val settings = firestoreSettings {
			setLocalCacheSettings(persistentCacheSettings {
				setSizeBytes(1024 * 1024 * 1024)
			})
		}
		Firebase.firestore.firestoreSettings = settings
	}
}