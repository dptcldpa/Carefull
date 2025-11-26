package com.cases.carefull.data.repository.diet

import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.repository.diet.BodyStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BodyStatsRepositoryImpl @Inject constructor(
    private val bmrDao: BmrDao
) : BodyStatsRepository {

    override fun getBmr(userId: String): Flow<Bmr?> {
        return bmrDao.findBmr(userId)
            .map { it?.toDomain() }
    }

    override suspend fun updateBmr(bmr: Bmr) {
        val bmrEntity = bmr.toEntity()
        bmrDao.insertBmr(bmrEntity)
    }
}
