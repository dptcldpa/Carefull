//package com.cases.carefull.data.repository
//
//import android.util.Log
//import com.cases.carefull.data.network.MedicineApiService
//import com.cases.carefull.data.network.MedicineItem
//import javax.inject.Inject
//
//class MedicineRepository @Inject constructor(
//    private val api: MedicineApiService,
//    private val serviceKey: String
//) {
//    suspend fun getMedicineByName(name: String): List<MedicineItem> {
//        return try {
//            Log.d("API_KEY_CHECK", "API Key: $serviceKey")
//            val response = api.getDrugInfo(serviceKey, name)
//            if (response.isSuccessful) {
//                val items = response.body()?.response?.body?.items ?: emptyList()
//                Log.d("MedicineRepository", "받아온 아이템 수: ${items.size}")
//                items
//            } else {
//                Log.e("MedicineRepository", "응답 실패: ${response.code()}")
//                emptyList()
//            }
//        } catch (e: Exception) {
//            Log.e("MedicineRepository", "예외 발생: ${e.message}")
//            emptyList()
//        }
//    }
//}
