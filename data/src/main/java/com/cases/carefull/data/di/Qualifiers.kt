package com.cases.carefull.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DietApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MedicineApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HospitalApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DietRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MedicineRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HospitalRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatbotApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatbotInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatbotOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatbotRetrofit