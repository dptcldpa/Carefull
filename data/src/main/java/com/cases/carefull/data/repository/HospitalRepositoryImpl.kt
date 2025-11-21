package com.cases.carefull.data.repository

import android.util.Log
import com.cases.carefull.data.datasource.HospitalDataSource
import com.cases.carefull.data.di.DepartmentCode
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.Hospital
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HospitalRepositoryImpl @Inject constructor(
    private val hospitalDataSource: HospitalDataSource,
    @DepartmentCode private val departmentCodes: Map<String, String>
) : HospitalRepository {
    override fun getHospitals(
        query: String,
        latitude: Double,
        longitude: Double
    ): Flow<DataResourceResult<List<Hospital>>> = flow {

        emit(DataResourceResult.Loading)

        val result = runCatching {
            val departmentCode = if (query.isBlank()) {
                ""
            } else {
                departmentCodes[query] ?: "01"
            }

            val hospitalDtoList = hospitalDataSource.getHospitalList(
                departmentCode, latitude, longitude
            )

            coroutineScope {
                val hospitalsWithEval = hospitalDtoList.map { hospitalDto ->
                    async {
                        val evalInfoList = hospitalDataSource.getHospitalExcell(hospitalDto.ykiho ?: "")
                        hospitalDto to evalInfoList
                    }
                }
                val combinedData = hospitalsWithEval.awaitAll()

                combinedData.map { (hospitalDto, evalList) ->
                    val targetEvaluationNames = getTargetEvaluationNamesFor(query)

                    val isExcellent = evalList.any { evalItem ->
                        val isTargetEval = evalItem.asmNm in targetEvaluationNames
                        val isGoodGrade = evalItem.asmGrd == "1"
                        isTargetEval && isGoodGrade
                    }

                    hospitalDto.toDomain(latitude, longitude, isExcellent, query)
                }
            }
        }

        result.fold(
            onSuccess = { hospitalDomainList ->
                emit(DataResourceResult.Success(hospitalDomainList))
            },
            onFailure = { exception ->
                Log.e("HospitalRepository", "XML Parsing Failed!", exception)
                exception.printStackTrace()
                emit(DataResourceResult.Error(exception))
            }
        )
    }

    private fun getTargetEvaluationNamesFor(department: String): List<String> {
        return when (department) {
            "내과" -> listOf(
                "고혈압·당뇨병",
                "만성폐쇄성폐질환",
                "천식",
                "폐렴",
                "혈액투석"
            )
            "신경과", "신경외과", "재활의학과" -> listOf(
                "급성기 뇌졸중"
            )
            "심장혈관흉부외과" -> listOf(
                "관상동맥우회술",
                "폐암"
            )
            "외과" -> listOf(
                "위암",
                "대장암",
                "유방암"
            )
            "산부인과" -> listOf(
                "유방암"
            )
            "이비인후과", "소아청소년과" -> listOf(
                "폐렴",
                "천식",
                "만성폐쇄성폐질환"
            )
            else -> emptyList()
        }
    }
}