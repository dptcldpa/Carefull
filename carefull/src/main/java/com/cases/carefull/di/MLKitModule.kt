package com.cases.carefull.di

import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLKitModule {
	
	@Provides
	@Singleton
	fun providePoseDetectorOptions(): PoseDetectorOptions {
		return PoseDetectorOptions.Builder()
			.setDetectorMode(PoseDetectorOptions.STREAM_MODE)
			.build()
	}
	
	@Provides
	@Singleton
	fun providePoseDetector(options: PoseDetectorOptions): PoseDetector {
		return PoseDetection.getClient(options)
	}
}