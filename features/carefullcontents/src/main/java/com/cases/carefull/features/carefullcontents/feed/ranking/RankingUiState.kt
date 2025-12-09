package com.cases.carefull.features.carefullcontents.feed.ranking

import com.cases.carefull.domain.model.feed.MyRankInfo
import com.cases.carefull.domain.model.feed.Ranker
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.features.carefullcontents.util.UiText

data class RankingUiState (
	val isLoading: Boolean = true,
	val error: UiText? = null,
	val selectedSport: ExerciseType = ExerciseType.DUMBBELL_CURL,
	
	val myRankInfo: MyRankInfo? = null,
	val rankingList: List<Ranker> = emptyList()
	)
