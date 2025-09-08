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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cases.carefull.domain.model.diet.BmrActivity
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.features.carefullcommon.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmrScreen(
	viewModel: DietViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()
	
	BasalMetabolicRateContent(
		bmrState = uiState.bmrState,
		isLoading = uiState.isLoading,
		isBmrChanged = uiState.isBmrChanged,
		onGenderSelected = viewModel::onGenderSelected,
		onHeightChanged = viewModel::onHeightChanged,
		onWeightChanged = viewModel::onWeightChanged,
		onAgeChanged = viewModel::onAgeChanged,
		onActivitySelected = viewModel::onActivitySelected,
		onSaveClicked = viewModel::onSaveClicked
	)
}

@Composable
fun EditableMenuButton(
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
			modifier = Modifier.width(100.dp),
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun BasalMetabolicRateContent(
	bmrState: BmrUiState,
	isLoading: Boolean,
	isBmrChanged: Boolean,
	onGenderSelected: (Gender) -> Unit,
	onHeightChanged: (String) -> Unit,
	onWeightChanged: (String) -> Unit,
	onAgeChanged: (String) -> Unit,
	onActivitySelected: (BmrActivity) -> Unit,
	onSaveClicked: () -> Unit
) {
	
	var isActivityMenuExpanded by remember { mutableStateOf(false) }
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
				selected = (bmrState.gender == Gender.MALE),
				onClick = { onGenderSelected(Gender.MALE) }
			)
			Text("남성")
			Spacer(Modifier.width(8.dp))
			RadioButton(
				selected = (bmrState.gender == Gender.FEMALE),
				onClick = { onGenderSelected(Gender.FEMALE) }
			)
			Text("여성")
		}
		Spacer(modifier = Modifier.height(16.dp))
		EditableMenuButton(
			label = stringResource(R.string.height),
			value = bmrState.height,
			onValueChange = onHeightChanged,
			unit = "cm"
		)
		EditableMenuButton(
			label = stringResource(R.string.weight),
			value = bmrState.weight,
			onValueChange = onWeightChanged,
			unit = "kg"
		)
		EditableMenuButton(
			label = stringResource(R.string.age),
			value = bmrState.age,
			onValueChange = onAgeChanged,
			unit = "세"
		)
		Spacer(modifier = Modifier.height(24.dp))
		ExposedDropdownMenuBox(
			expanded = isActivityMenuExpanded,
			onExpandedChange = { isActivityMenuExpanded = !isActivityMenuExpanded },
			modifier = Modifier.padding(horizontal = 32.dp)
		) {
			OutlinedTextField(
				value = bmrState.activity.description,
				onValueChange = {},
				readOnly = true,
				label = { Text(stringResource(R.string.activity_setting)) },
				trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isActivityMenuExpanded) },
				modifier = Modifier
					.menuAnchor(type = MenuAnchorType.PrimaryEditable)
					.fillMaxWidth(),
				textStyle = LocalTextStyle.current.copy(
					fontSize = 14.sp
				)
			)
			ExposedDropdownMenu(
				expanded = isActivityMenuExpanded,
				onDismissRequest = { isActivityMenuExpanded = false }
			) {
				BmrActivity.entries.forEach { activity ->
					DropdownMenuItem(
						text = { Text(text = activity.description) },
						onClick = {
							onActivitySelected(activity)
							isActivityMenuExpanded = false
						}
					)
				}
			}
		}
		Spacer(modifier = Modifier.height(24.dp))
		ResultRow(
			label = stringResource(R.string.basal_metabolic_rate),
			value = bmrState.calculatedBmr.toString()
		)
		Spacer(modifier = Modifier.height(16.dp))
		ResultRow(
			label = stringResource(R.string.total_daily_energy),
			value = bmrState.activityMetabolism.toString(),
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
			enabled = !isLoading &&
					bmrState.height.isNotBlank() &&
					bmrState.weight.isNotBlank() &&
					bmrState.age.isNotBlank() &&
					isBmrChanged
		) {
			Text(
				text = stringResource(R.string.save),
				style = MaterialTheme.typography.bodyLarge
			)
		}
	}
}

@Composable
private fun ResultRow(label: String, value: String, isHighlighted: Boolean = false) {
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