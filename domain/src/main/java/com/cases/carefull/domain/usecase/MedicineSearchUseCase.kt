package com.cases.carefull.domain.usecase

import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


//class MedicineSearchUseCase(
//    private val repository: MedicineRepository,
//    private val medicineApiKey: String
//) {
//    suspend operator fun invoke(query: String): Result<List<MedicineItem>> {
//        return repository.searchMedicines(
//            medicineApiKey = this.medicineApiKey, // 클래스의 멤버 변수 사용
//            query = query
//        )
//    }
//}

class MedicineSearchUseCase(
    private val repository: MedicineRepository,
    private val medicineApiKey: String
) {
    operator fun invoke(query: String): Flow<List<MedicineItem>> = flow {
        val items = repository.searchMedicines(
            medicineApiKey = medicineApiKey,
            query = query
        )
        emit(items)
    }
}