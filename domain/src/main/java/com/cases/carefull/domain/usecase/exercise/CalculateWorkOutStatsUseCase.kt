package com.cases.carefull.domain.usecase.exercise

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseStatistics
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.util.WorkoutDateUtils

class CalculateWorkOutStatsUseCase {
    operator fun invoke(
        exercises: List<ExerciseCollection>
    ): List<ExerciseStatistics> {

        val dailyKey = WorkoutDateUtils.getDailyKey()
        val weekKey = WorkoutDateUtils.getWeeklyKey()

        return ExerciseType.entries.map { type ->
            val relevantCollections = exercises.filter { it.exerciseType == type }

            val totalCount = relevantCollections.sumOf { it.count }
            val weeklyCount = relevantCollections.sumOf { it.weeklyCounts[weekKey] ?: 0 }
            val dailyCount = relevantCollections.sumOf { it.dailyCounts[dailyKey] ?: 0 }

            ExerciseStatistics(
                type = type,
                totalCount = totalCount,
                weeklyCount = weeklyCount,
                dailyCount = dailyCount
            )
        }
    }
}
