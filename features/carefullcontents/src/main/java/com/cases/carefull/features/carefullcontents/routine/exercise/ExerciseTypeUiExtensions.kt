package com.cases.carefull.features.carefullcontents.routine.exercise

import androidx.annotation.StringRes
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.R

val ExerciseType.descriptionResId: Int
    @StringRes get() = when (this) {
        ExerciseType.DUMBBELL_CURL -> R.string.workout_desc_dumbbell_curl
        ExerciseType.SQUAT -> R.string.workout_desc_squat
        ExerciseType.PUSH_UP -> R.string.workout_desc_pushup
        ExerciseType.DUMBBELL_SHOULDER_PRESS -> R.string.workout_desc_shoulder_press
    }
