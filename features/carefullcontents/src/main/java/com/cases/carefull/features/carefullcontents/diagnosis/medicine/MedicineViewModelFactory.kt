package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.domain.repository.MedicineRepository

//class MedicineViewModelFactory(
//    private val medicineRepository: MedicineRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return MedicineViewModel(medicineRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}