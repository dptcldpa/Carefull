package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.routine.ExerciseCollectionDto

interface RankingDataSource {
    suspend fun getTopRankings(sportName: String): List<ExerciseCollectionDto>
    suspend fun getMyRecord(userId: String, sportName: String): ExerciseCollectionDto?
    suspend fun getCountGreaterThan(sportName: String, score: Int): Long
}
