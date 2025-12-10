package com.cases.carefull.features.carefullcontents.feed.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.feed.FeedException
import com.cases.carefull.domain.model.feed.MyRankInfo
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.repository.feed.RankingRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.features.carefullcontents.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val rankingRepository: RankingRepository
) : ViewModel() {
    private val _selectedSport = MutableStateFlow(ExerciseType.DUMBBELL_CURL)

    private val postId: String = "CareFull"

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<RankingUiState> = _selectedSport
        .flatMapLatest { sport ->
            combine(
                flow { emit(rankingRepository.getRankingList(sport)) },
                flow { emit(fetchMyRanking(sport)) }
            ) { rankingResult, myRankResult ->
                if (rankingResult is DataResourceResult.Success &&
                    myRankResult is DataResourceResult.Success
                ) {
                    RankingUiState(
                        isLoading = false,
                        selectedSport = sport,
                        rankingList = rankingResult.data,
                        myRankInfo = myRankResult.data,
                        error = null
                    )
                } else {
                    val exception = (rankingResult as? DataResourceResult.Error)?.exception
                        ?: (myRankResult as? DataResourceResult.Error)?.exception
                        ?:  FeedException.Unknown

                    RankingUiState(
                        isLoading = false,
                        selectedSport = sport,
                        error = exception.asUiText()
                    )
                }
            }
                .onStart {
                    emit(RankingUiState(isLoading = true, selectedSport = sport))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RankingUiState(
                isLoading = true,
                selectedSport = ExerciseType.DUMBBELL_CURL
            )
        )

    private suspend fun fetchMyRanking(sport: ExerciseType): DataResourceResult<MyRankInfo> {
        val currentUserId = postId
        return if (currentUserId != null) {
            rankingRepository.getMyRanking(currentUserId, sport)
        } else {
            DataResourceResult.Error(FeedException.Unauthorized)
        }
    }

    fun onSportSelected(sport: ExerciseType) {
        _selectedSport.value = sport
    }
}
