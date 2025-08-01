package com.cases.carefull.di

import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.BuildConfig
import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.data.network.RetrofitInstance
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcommon.components.NavigationRepositoryImpl
import com.cases.carefull.features.carefullcommon.model.NavigationRepository

interface AppContainer {
    val navigationRepository: NavigationRepository
    val medicineRepository: MedicineRepository
    val medicineSearchUseCase: MedicineSearchUseCase
    val medicineViewModelFactory: ViewModelProvider.Factory
}

class DefaultAppContainer : AppContainer {

    override val navigationRepository: NavigationRepository by lazy {
        NavigationRepositoryImpl()
    }

    private val medicineApiService by lazy {
        RetrofitInstance.medicineApi
    }

    override val medicineRepository: MedicineRepository by lazy {
        MedicineRepositoryImpl(medicineApiService)
    }

    override val medicineSearchUseCase: MedicineSearchUseCase by lazy {
        MedicineSearchUseCase(medicineRepository)
    }

    override val medicineViewModelFactory: ViewModelProvider.Factory by lazy {
        MainViewModelFactory(
            navigationRepository = navigationRepository, // navigationRepository도 AppContainer에 있다고 가정
            medicineSearchUseCase = medicineSearchUseCase, // AppContainer가 가진 UseCase 전달
            // AppContainer가 BuildConfig에 직접 접근하여 API 키를 가져옵니다.
            medicineApiKey = BuildConfig.medicine_api_key
        )
    }
}