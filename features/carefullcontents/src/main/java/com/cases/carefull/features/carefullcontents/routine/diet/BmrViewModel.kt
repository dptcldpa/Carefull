package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.BmrMovementLevel
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.domain.usecase.bmr.CalculateBmrUseCase
import com.cases.carefull.domain.usecase.bmr.GetSavedBmrUseCase
import com.cases.carefull.domain.usecase.bmr.SaveBmrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class BmrViewModel @Inject constructor(
    private val calculateBmrUseCase: CalculateBmrUseCase,
    private val saveBmrUseCase: SaveBmrUseCase,
    private val getSavedBmrUseCase: GetSavedBmrUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BmrUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMyBmr()
    }

    private fun loadMyBmr() {
        viewModelScope.launch {
            getSavedBmrUseCase("test").collect { savedBmr ->
                if (savedBmr != null) {
                    val initialUiState = BmrUiState(
                        savedBmr = savedBmr,
                        gender = if (savedBmr.gender) Gender.MALE else Gender.FEMALE,
                        height = savedBmr.height.toString(),
                        weight = savedBmr.weight.toString(),
                        age = savedBmr.age.toString(),
                        movementLevel = savedBmr.movementLevel,
                        isLoading = false,
                        isError = false
                    )
                    _uiState.value = initialUiState
                    calculateMetabolism()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
        calculateMetabolism()
    }

    fun onHeightChanged(height: String) {
        if (height.all { it.isDigit() }) {
            _uiState.update { it.copy(height = height) }
            calculateMetabolism()
        }
    }

    fun onWeightChanged(weight: String) {
        if (weight.all { it.isDigit() }) {
            _uiState.update { it.copy(weight = weight) }
            calculateMetabolism()
        }
    }

    fun onAgeChanged(age: String) {
        if (age.all { it.isDigit() }) {
            _uiState.update { it.copy(age = age) }
            calculateMetabolism()
        }
    }

    fun onMovementLevelSelected(movementLevel: BmrMovementLevel) {
        _uiState.update { it.copy(movementLevel = movementLevel) }
        calculateMetabolism()
    }

    //미펜-세인트 조르 공식 (Mifflin-St Jeor Equation)
    fun calculateMetabolism() {
        val bmrInputState = _uiState.value
        val height = bmrInputState.height.toIntOrNull() ?: 0
        val weight = bmrInputState.weight.toIntOrNull() ?: 0
        val age = bmrInputState.age.toIntOrNull() ?: 0

        val result = calculateBmrUseCase(
            gender = bmrInputState.gender,
            height = height,
            weight = weight,
            age = age,
            movementLevel = bmrInputState.movementLevel
        )

        _uiState.update {
            it.copy(
                calculatedBmr = result.bmr,
                movementLevelMetabolism = result.tdee
            )
        }
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
                userId = "test",
                gender = (currentState.gender == Gender.MALE),
                age = ageInt,
                height = heightInt,
                weight = weightInt,
                movementLevel = currentState.movementLevel,
                bmr = currentState.calculatedBmr,
                movementLevelBmr = currentState.movementLevelMetabolism
            )
            saveBmrUseCase(bmrToSave)
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}