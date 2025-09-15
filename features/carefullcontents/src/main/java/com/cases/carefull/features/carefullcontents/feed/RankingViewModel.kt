package com.cases.carefull.features.carefullcontents.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.repository.RankingRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RankingViewModel @Inject constructor(
	private val rankingRepository: RankingRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(RankingUiState())
	val uiState = _uiState.asStateFlow()
	
	init {
		fetchRankingList(_uiState.value.selectedSport)
	}
	
	fun onSportSelected(sport: ExerciseType) {
		if (sport == _uiState.value.selectedSport && !_uiState.value.isError) return
		fetchRankingList(sport)
	}
	fun fetchRankingList(sport: ExerciseType) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, selectedSport = sport, isError = false) }
			val rankingListDeferred = async { rankingRepository.getRankingList(sport) }
			val myRankDeferred = async { rankingRepository.getMyRanking("CareFull", sport) }
			
			val rankingListResult = rankingListDeferred.await()
			val myRankResult = myRankDeferred.await()
			
			if (rankingListResult is DataResourceResult.Success && myRankResult is DataResourceResult.Success) {
				_uiState.update {
					it.copy(
						isLoading = false,
						rankingList = rankingListResult.data,
						myRankInfo = myRankResult.data
					)
				}
			} else {
				_uiState.update { it.copy(isLoading = false, isError = true) }
			}
		}
	}
}