package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.diet.BmrMovementLevel
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BmrRoute(
    viewModel: BmrViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (uiState.gender == Gender.MALE),
                onClick = { onGenderSelected(Gender.MALE) }
            )
            Text("남성")
            Spacer(Modifier.width(8.dp))
            RadioButton(
                selected = (uiState.gender == Gender.FEMALE),
                onClick = { onGenderSelected(Gender.FEMALE) }
            )
            Text("여성")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextFieldComponent(
            label = stringResource(R.string.height),
            value = uiState.height,
            onValueChange = onHeightChanged,
            unit = "cm"
        )
        OutlinedTextFieldComponent(
            label = stringResource(R.string.weight),
            value = uiState.weight,
            onValueChange = onWeightChanged,
            unit = "kg"
        )
        OutlinedTextFieldComponent(
            label = stringResource(R.string.age),
            value = uiState.age,
            onValueChange = onAgeChanged,
            unit = "세"
        )
        Spacer(modifier = Modifier.height(24.dp))
        ExposedDropdownMenuBox(
            expanded = isMovementLevelMenuExpanded,
            onExpandedChange = { isMovementLevelMenuExpanded = !isMovementLevelMenuExpanded },
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            OutlinedTextField(
                value = uiState.movementLevel.description,
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
                        text = { Text(text = movementLevel.description) },
                        onClick = {
                            onMovementLevelSelected(movementLevel)
                            isMovementLevelMenuExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ResultRowComponent(
            label = stringResource(R.string.basal_metabolic_rate),
            value = uiState.calculatedBmr.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        ResultRowComponent(
            label = stringResource(R.string.total_daily_energy),
            value = uiState.movementLevelMetabolism.toString(),
            isHighlighted = true
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSaveClicked,
            modifier = Modifier
				.fillMaxWidth(0.8f)
				.height(56.dp)
				.padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            ),
            enabled = !uiState.isLoading &&
                    uiState.height.isNotBlank() &&
                    uiState.weight.isNotBlank() &&
                    uiState.age.isNotBlank() &&
                    uiState.isBmrChanged
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun OutlinedTextFieldComponent(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(80.dp)
                .padding(top = 5.dp, bottom = 5.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
fun ResultRowComponent(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.kcal),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
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

@Preview(showBackground = true)
@Composable
fun OutlinedTextFieldComponentPreview() {
    CarefullTheme {
        OutlinedTextFieldComponent(
            label = "신장",
            value = "180",
            onValueChange = {},
            unit = "cm"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultRowComponentPreview() {
    CarefullTheme {
        ResultRowComponent(
            label = "기초 대사량",
            value = "1767",
            isHighlighted = true
        )
    }
}