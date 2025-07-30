package com.cases.carefull.di

import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.RetrofitInstance
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcommon.components.NavigationRepositoryImpl
import com.cases.carefull.features.carefullcommon.model.NavigationRepository

interface AppContainer {
    val medicineRepository: MedicineRepository
    val medicineSearchUseCase: MedicineSearchUseCase
    val navigationRepository: NavigationRepository
}

class DefaultAppContainer : AppContainer {

    private val apiService by lazy {
        RetrofitInstance.medicineApi
    }

    override val medicineRepository: MedicineRepository by lazy {
        MedicineRepositoryImpl(apiService)
    }

    override val medicineSearchUseCase: MedicineSearchUseCase by lazy {
        MedicineSearchUseCase(medicineRepository)
    }

    override val navigationRepository: NavigationRepository by lazy {
        NavigationRepositoryImpl()
    }
}