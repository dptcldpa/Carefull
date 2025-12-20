package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.domain.usecase.routine.diet.BmrUseCases
import com.cases.carefull.domain.util.DataResourceResult
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
    private val bmrUseCases: BmrUseCases
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
            bmrUseCases.getSavedBmr(userId).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is DataResourceResult.Success -> {
                        val bmrData = result.data

                        if (bmrData != null) {
                            val genderEnum = if (bmrData.gender) Gender.MALE else Gender.FEMALE
                            val calculationResult = bmrUseCases.calculateBmr(
                                gender = genderEnum,
                                height = bmrData.height,
                                weight = bmrData.weight,
                                age = bmrData.age,
                                movementLevel = bmrData.movementLevel
                            )

                            _uiState.update {
                                BmrUiState(
                                    savedBmr = bmrData,
                                    gender = genderEnum,
                                    height = bmrData.height.toString(),
                                    weight = bmrData.weight.toString(),
                                    age = bmrData.age.toString(),
                                    movementLevel = bmrData.movementLevel,
                                    calculatedBmr = calculationResult.bmr,
                                    movementLevelMetabolism = calculationResult.tdee,
                                    isLoading = false,
                                    isError = false
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }

                    is DataResourceResult.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, isError = true)
                        }
                    }
                }
            }
        }
    }

    private fun updateStateWithCalculation(
        gender: Gender = _uiState.value.gender,
        height: String = _uiState.value.height,
        weight: String = _uiState.value.weight,
        age: String = _uiState.value.age,
        movementLevel: BmrMovementLevel = _uiState.value.movementLevel
    ) {
        val heightInt = height.toIntOrNull() ?: 0
        val weightInt = weight.toIntOrNull() ?: 0
        val ageInt = age.toIntOrNull() ?: 0

        val result = bmrUseCases.calculateBmr(
            gender = gender,
            height = heightInt,
            weight = weightInt,
            age = ageInt,
            movementLevel = movementLevel
        )

        _uiState.update {
            it.copy(
                gender = gender,
                height = height,
                weight = weight,
                age = age,
                movementLevel = movementLevel,
                calculatedBmr = result.bmr,
                movementLevelMetabolism = result.tdee
            )
        }
    }

    fun onGenderSelected(gender: Gender) {
        updateStateWithCalculation(gender = gender)
    }

    fun onHeightChanged(height: String) {
        if (!height.all { it.isDigit() }) return
        updateStateWithCalculation(height = height)
    }

    fun onWeightChanged(weight: String) {
        if (weight.all { it.isDigit() }) {
            updateStateWithCalculation(weight = weight)
        }
    }

    fun onAgeChanged(age: String) {
        if (!age.all { it.isDigit() }) return
        updateStateWithCalculation(age = age)
    }

    fun onMovementLevelSelected(movementLevel: BmrMovementLevel) {
        updateStateWithCalculation(movementLevel = movementLevel)
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
            bmrUseCases.saveBmr(bmrToSave)
            _uiState.update { it.copy(isLoading = false) }
            _eventFlow.emit(UiEvent.ShowToast("신체 정보가 저장되었습니다."))
        }
    }
}

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
}