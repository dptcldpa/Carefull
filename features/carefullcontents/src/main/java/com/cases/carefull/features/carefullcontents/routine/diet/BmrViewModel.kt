package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.domain.usecase.routine.diet.GetSavedBmrUseCase
import com.cases.carefull.domain.usecase.routine.diet.SaveBmrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BmrViewModel @Inject constructor(
    private val saveBmrUseCase: SaveBmrUseCase,
    private val getSavedBmrUseCase: GetSavedBmrUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BmrUiState())
    val uiState = _uiState.asStateFlow()
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val userId = "test"

    init {
        loadMyBmr()
    }

    private fun loadMyBmr() {
        viewModelScope.launch {
            getSavedBmrUseCase(userId).collectLatest { savedBmr ->
                if (savedBmr != null) {
                    _uiState.update {
                        BmrUiState(
                            savedBmr = savedBmr,
                            gender = if (savedBmr.gender) Gender.MALE else Gender.FEMALE,
                            height = savedBmr.height.toString(),
                            weight = savedBmr.weight.toString(),
                            age = savedBmr.age.toString(),
                            movementLevel = savedBmr.movementLevel,
                            isLoading = false,
                            isError = false
                        ).recalculate()
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = gender).recalculate() }
    }

    fun onHeightChanged(height: String) {
        if (!height.all { it.isDigit() }) return
        _uiState.update { it.copy(height = height).recalculate() }
    }

    fun onWeightChanged(weight: String) {
        if (weight.all { it.isDigit() }) {
            if (!weight.all { it.isDigit() }) return
            _uiState.update { it.copy(weight = weight).recalculate() }
        }
    }

    fun onAgeChanged(age: String) {
        if (!age.all { it.isDigit() }) return
        _uiState.update { it.copy(age = age).recalculate() }
    }

    fun onMovementLevelSelected(movementLevel: BmrMovementLevel) {
        _uiState.update { it.copy(movementLevel = movementLevel).recalculate() }
    }

    fun onSaveClicked() {
        val currentState = _uiState.value
        val heightInt = currentState.height.toIntOrNull()
        val weightInt = currentState.weight.toIntOrNull()
        val ageInt = currentState.age.toIntOrNull()

        if (heightInt == null || weightInt == null || ageInt == null ||
            heightInt <= 0 || weightInt <= 0 || ageInt <= 0
        ) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val bmrToSave = Bmr(
                userId = userId,
                gender = (currentState.gender == Gender.MALE),
                age = ageInt,
                height = heightInt,
                weight = weightInt,
                movementLevel = currentState.movementLevel,
                bmr = currentState.calculatedBmr,
                tdee = currentState.movementLevelMetabolism
            )
            saveBmrUseCase(bmrToSave)
            _uiState.update { it.copy(isLoading = false) }
            _eventFlow.emit(UiEvent.ShowToast("신체 정보가 저장되었습니다."))
        }
    }
}

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
}