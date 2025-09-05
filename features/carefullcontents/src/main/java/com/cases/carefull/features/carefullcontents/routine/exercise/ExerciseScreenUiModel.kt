package com.cases.carefull.features.carefullcontents.routine.exercise

import androidx.annotation.DrawableRes
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.R

data class ExerciseUiModel(
    val type: ExerciseType,
    val name: String,
    @param:DrawableRes
    val imageResId: Int,
    val description: String,
    val totalCount: Int = 0,
    //
    val weeklyCount: Int = 0,
    val dailyCount: Int = 0
)

fun ExerciseType.toUiModel(totalCount: Int, weeklyCount: Int, dailyCount: Int): ExerciseUiModel {
    return when (this) {
        ExerciseType.DUMBBELL_CURL -> ExerciseUiModel(
            type = this,
            name = "덤벨 컬",
            imageResId = R.drawable.dumbbell_curl,
            description = "이두근을 발달시키는 대표적인 운동으로,덤벨을 이용해 팔을 굽혔다 펴는 동작을 반복합니다.",
            totalCount = totalCount,
            weeklyCount = weeklyCount,
            dailyCount = dailyCount
        )
        ExerciseType.SQUAT -> ExerciseUiModel(
            type = this,
            name = "스쿼트",
            imageResId = R.drawable.squat,
            description = "쪼그려 앉는 동작을 반복하는 웨이트 트레이닝 운동입니다. 양발을 어깨 너비로 벌리고 서서 무릎을 굽혔다 펴는 동작을 통해 하체 근육을 강화합니다.",
            totalCount = totalCount,
            weeklyCount = weeklyCount,
            dailyCount = dailyCount
        )
        ExerciseType.PUSH_UP -> ExerciseUiModel(
            type = this,
            name = "푸쉬업",
            imageResId = R.drawable.push_up,
            description = "엎드린 자세에서 손과 발을 지지점으로 하여 몸을 바닥에서 밀어 올리는 전신 운동입니다.가슴, 어깨, 삼두근, 코어 근육 등을 발달에 효과적입니다.",
            totalCount = totalCount,
            weeklyCount = weeklyCount,
            dailyCount = dailyCount
        )
        ExerciseType.DUMBBELL_SHOULDER_PRESS -> ExerciseUiModel(
            type = this,
            name = "덤벨 숄더 프레스",
            imageResId = R.drawable.dumbbell_shoulder_press,
            description = "어깨 근육을 발달시키는 대표적인 운동으로, 덤벨을 이용하여 양쪽 어깨를 동시에 또는 번갈아 가며 머리 위로 밀어 올리는 동작으로, 어깨 근육을 키우는 데 효과적입니다. ",
            totalCount = totalCount,
            weeklyCount = weeklyCount,
            dailyCount = dailyCount
        )
    }
}