package com.cases.carefull.features.carefullcontents.routine.exercise

import androidx.annotation.DrawableRes
import com.cases.carefull.domain.model.exercise.ExerciseStatistics
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.R

data class ExerciseUiModel(
    val type: ExerciseType,
    @param:DrawableRes
    val imageResId: Int,
    val totalCount: Int = 0,
    val weeklyCount: Int = 0,
    val dailyCount: Int = 0
)
fun ExerciseStatistics.toUiModel(): ExerciseUiModel {
    return ExerciseUiModel(
        type = this.type,
        imageResId = when (this.type) {
            ExerciseType.DUMBBELL_CURL -> R.drawable.dumbbell_curl
            ExerciseType.SQUAT -> R.drawable.squat
            ExerciseType.PUSH_UP -> R.drawable.push_up
            ExerciseType.DUMBBELL_SHOULDER_PRESS -> R.drawable.dumbbell_shoulder_press
        },
        totalCount = this.totalCount,
        weeklyCount = this.weeklyCount,
        dailyCount = this.dailyCount
    )
}
