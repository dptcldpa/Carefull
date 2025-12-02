package com.cases.carefull.domain.model.routine.diet

enum class BmrMovementLevel(
    val multiplier: Double
) {
    NONE(1.2),
    LIGHT(1.375),
    MEDIUM(1.55),
    HEAVY(1.725),
    EXTREME(1.9);
}
