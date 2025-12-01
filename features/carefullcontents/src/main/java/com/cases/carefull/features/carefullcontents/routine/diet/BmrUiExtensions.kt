package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.annotation.StringRes
import com.cases.carefull.domain.model.diet.BmrMovementLevel
import com.cases.carefull.features.carefullcommon.R

val BmrMovementLevel.descriptionResId: Int
    @StringRes get() = when (this) {
        BmrMovementLevel.NONE -> R.string.bmr_level_none
        BmrMovementLevel.LIGHT -> R.string.bmr_level_light
        BmrMovementLevel.MEDIUM -> R.string.bmr_level_medium
        BmrMovementLevel.HEAVY -> R.string.bmr_level_heavy
        BmrMovementLevel.EXTREME -> R.string.bmr_level_extreme
    }
