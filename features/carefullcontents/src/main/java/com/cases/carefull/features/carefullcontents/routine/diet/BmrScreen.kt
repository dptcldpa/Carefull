package com.cases.carefull.features.carefullcontents.routine.diet

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.components.SubmitButton
import com.cases.carefull.features.carefullcommon.components.LabelValueRow
import com.cases.carefull.features.carefullcommon.components.UnitTextField
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BmrRoute(
    viewModel: BmrViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BmrScreen(
        uiState = uiState,
        onGenderSelected = viewModel::onGenderSelected,
        onHeightChanged = viewModel::onHeightChanged,
        onWeightChanged = viewModel::onWeightChanged,
        onAgeChanged = viewModel::onAgeChanged,
        onMovementLevelSelected = viewModel::onMovementLevelSelected,
        onSaveClicked = viewModel::onSaveClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmrScreen(
    uiState: BmrUiState,
    onGenderSelected: (Gender) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onMovementLevelSelected: (BmrMovementLevel) -> Unit,
    onSaveClicked: () -> Unit
) {
    var isMovementLevelMenuExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.basal_metabolic_rate_measurement),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.gender),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(
                selected = (uiState.gender == Gender.MALE),
                onClick = { onGenderSelected(Gender.MALE) }
            )
            Text(text = stringResource(id = R.string.gender_male))
            Spacer(Modifier.width(8.dp))
            RadioButton(
                selected = (uiState.gender == Gender.FEMALE),
                onClick = { onGenderSelected(Gender.FEMALE) }
            )
            Text(text = stringResource(id = R.string.gender_female))
        }
        UnitTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.height),
            value = uiState.height,
            onValueChange = onHeightChanged,
            unit = stringResource(id = R.string.unit_cm)
        )
        UnitTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.weight),
            value = uiState.weight,
            onValueChange = onWeightChanged,
            unit = stringResource(id = R.string.unit_kg)
        )
        UnitTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.age),
            value = uiState.age,
            onValueChange = onAgeChanged,
            unit = stringResource(id = R.string.unit_age)
        )
        Spacer(modifier = Modifier.height(24.dp))
        ExposedDropdownMenuBox(
            expanded = isMovementLevelMenuExpanded,
            onExpandedChange = { isMovementLevelMenuExpanded = !isMovementLevelMenuExpanded },
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            OutlinedTextField(
                value = stringResource(uiState.movementLevel.descriptionResId),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.movementlevel_setting)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMovementLevelMenuExpanded) },
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp
                )
            )
            ExposedDropdownMenu(
                expanded = isMovementLevelMenuExpanded,
                onDismissRequest = { isMovementLevelMenuExpanded = false }
            ) {
                BmrMovementLevel.entries.forEach { movementLevel ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = movementLevel.descriptionResId)) },
                        onClick = {
                            onMovementLevelSelected(movementLevel)
                            isMovementLevelMenuExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        LabelValueRow(
            label = stringResource(R.string.basal_metabolic_rate),
            unit = stringResource(R.string.unit_kcal),
            value = uiState.calculatedBmr.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LabelValueRow(
            label = stringResource(R.string.total_daily_energy),
            unit = stringResource(R.string.unit_kcal),
            value = uiState.movementLevelMetabolism.toString(),
            isHighlighted = true
        )
        Spacer(modifier = Modifier.weight(1f))
        SubmitButton(
            text = stringResource(R.string.common_save),
            onClick = onSaveClicked,
            enabled = !uiState.isLoading &&
                    uiState.height.isNotBlank() &&
                    uiState.weight.isNotBlank() &&
                    uiState.age.isNotBlank() &&
                    uiState.isBmrChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BmrScreenPreview() {
    val fakeUiState = BmrUiState(
        height = "180",
        weight = "75",
        age = "30",
        calculatedBmr = 1767,
        movementLevelMetabolism = 2429,
        isLoading = false,
        movementLevel = BmrMovementLevel.NONE
    )
    CarefullTheme {
        BmrScreen(
            uiState = fakeUiState,
            onGenderSelected = {},
            onHeightChanged = {},
            onWeightChanged = {},
            onAgeChanged = {},
            onMovementLevelSelected = {},
            onSaveClicked = {}
        )
    }
}
