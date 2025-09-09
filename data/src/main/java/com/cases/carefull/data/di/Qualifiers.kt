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
annotation class DietRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MedicineRetrofit