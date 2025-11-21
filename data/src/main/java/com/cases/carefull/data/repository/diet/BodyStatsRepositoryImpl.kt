package com.cases.carefull.data.repository.diet

import android.content.Context
import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.database.AppDatabase
import com.cases.carefull.data.mapper.toDataModel
import com.cases.carefull.data.mapper.toDomainModel
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.repository.diet.BodyStatsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BodyStatsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BodyStatsRepository {
    private val bmrDao: BmrDao = AppDatabase.Companion.getInstance(context).bmrDao()

    override fun getMyBmr(userId: String): Flow<Bmr?> {
        return bmrDao.getBmrByUserId(userId).map { bmrCollection ->
            bmrCollection?.toDomainModel()
        }
    }

    override suspend fun updateMyBmr(bmr: Bmr) {
        val bmrCollection = bmr.toDataModel()
        bmrDao.insertBmr(bmrCollection)
    }
}