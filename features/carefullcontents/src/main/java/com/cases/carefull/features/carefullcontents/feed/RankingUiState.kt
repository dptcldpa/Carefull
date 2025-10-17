package com.cases.carefull.features.carefullcontents.feed

import com.cases.carefull.domain.model.MyRankInfo
import com.cases.carefull.domain.model.Ranker
import com.cases.carefull.domain.model.exercise.ExerciseType

data class RankingUiState (
	val isLoading: Boolean = true,
	val isError: Boolean = false,
	val selectedSport: ExerciseType = ExerciseType.DUMBBELL_CURL,
	
	val myRankInfo: MyRankInfo? = null,
	val rankingList: List<Ranker> = emptyList()
	)