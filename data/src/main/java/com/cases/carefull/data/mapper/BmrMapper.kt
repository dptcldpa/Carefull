package com.cases.carefull.data.mapper

import com.cases.carefull.data.entity.BmrEntity
import com.cases.carefull.domain.model.routine.diet.Bmr

fun BmrEntity.toDomain(): Bmr {
    return Bmr(
        userId = this.userId,
        gender = this.gender,
        age = this.age,
        height = this.height,
        weight = this.weight,
        movementLevel = this.movementLevel,
        bmr = this.bmr,
        tdee = this.movementLevelBmr
    )
}

fun Bmr.toEntity(): BmrEntity {
    return BmrEntity(
        userId = this.userId,
        gender = this.gender,
        age = this.age,
        height = this.height,
        weight = this.weight,
        movementLevel = this.movementLevel,
        bmr = this.bmr,
        movementLevelBmr = this.tdee
    )
}
