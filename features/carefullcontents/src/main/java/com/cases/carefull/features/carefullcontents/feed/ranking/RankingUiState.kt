package com.cases.carefull.features.carefullcontents.feed.ranking

import com.cases.carefull.domain.model.feed.MyRankInfo
import com.cases.carefull.domain.model.feed.Ranker
import com.cases.carefull.domain.model.routine.exercise.ExerciseType

data class RankingUiState (
	val isLoading: Boolean = true,
	val isError: Boolean = false,
	val selectedSport: ExerciseType = ExerciseType.DUMBBELL_CURL,
	
	val myRankInfo: MyRankInfo? = null,
	val rankingList: List<Ranker> = emptyList()
	)