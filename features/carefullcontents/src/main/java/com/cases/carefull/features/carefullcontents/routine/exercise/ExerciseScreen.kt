package com.cases.carefull.features.carefullcontents.routine.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute

@Composable
fun ExerciseScreen(
	viewModel: ExerciseViewModel,
	navController: NavController
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val exerciseList by viewModel.exerciseListForUi.collectAsStateWithLifecycle()
	
	if (uiState.showDialog && uiState.selectedExercise != null) {
		ExerciseCountDialog(
			exerciseName = uiState.selectedExercise!!,
			onDismiss = { viewModel.onDialogDismiss() },
			onConfirm = { count ->
				viewModel.onDialogConfirm()
				navController.navigate(
					RoutineRoute.WorkOutScreen(
						exerciseType = uiState.selectedExercise!!,
						count = count
					)
				)
			}
		)
	}
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "운동을 선택하세요.",
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Bold,
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 24.dp, bottom = 16.dp),
			textAlign = TextAlign.Start
		)
		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			items(
				items = exerciseList,
				key = { it.type.name }
			) { exerciseUiModel ->
				ExerciseCard(
					uiModel = exerciseUiModel,
					onClick = {
						viewModel.onExerciseSelected(exerciseUiModel.type)
					}
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
	uiModel: ExerciseUiModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		onClick = onClick,
		modifier = modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Row(
			modifier = Modifier
				.padding(8.dp)
				.height(IntrinsicSize.Min),
			verticalAlignment = Alignment.CenterVertically
		) {
			Image(
				painter = painterResource(id = uiModel.imageResId),
				contentDescription = "${uiModel.type} 이미지",
				modifier = Modifier
					.width(100.dp)
					.fillMaxHeight()
					.clip(RoundedCornerShape(16.dp)),
				contentScale = ContentScale.Fit
			)
			Spacer(modifier = Modifier.width(16.dp))
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = uiModel.name,
					style = MaterialTheme.typography.titleLarge
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = uiModel.description,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "총 ${uiModel.totalCount}회 수행",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.primary,
					fontWeight = FontWeight.SemiBold
				)
			}
		}
	}
}

@Composable
fun ExerciseCountDialog(
	exerciseName: ExerciseType,
	onDismiss: () -> Unit,
	onConfirm: (Int) -> Unit
) {
	var count by remember { mutableIntStateOf(10) }
	
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
		)
	) {
		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = MaterialTheme.shapes.large,
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surface,
				contentColor = MaterialTheme.colorScheme.onSurface
			)
		) {
			Column(
				modifier = Modifier.padding(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Row {
					Spacer(modifier = Modifier.weight(0.5f))
					Text(
						text = exerciseName.type,
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold
					)
					Spacer(modifier = Modifier.weight(0.25f))
					TextButton(onClick = onDismiss) {
						Text(
							text = "닫기",
							style = MaterialTheme.typography.labelLarge,
							color = Color.Gray,
							modifier = Modifier.padding(bottom = 10.dp)
						)
					}
				}
				Row {
					Text(
						text = "$count",
						style = MaterialTheme.typography.titleLarge
					)
					Spacer(modifier = Modifier.width(5.dp))
					Text(
						text = "회",
						style = MaterialTheme.typography.titleLarge,
					)
				}
				
				Row(
					verticalAlignment = Alignment.Bottom,
					horizontalArrangement = Arrangement.Center
				) {
					Button(
						onClick = {
							if (count > 4) repeat(5) {
								count--
							}
						},
						modifier = Modifier.size(40.dp),
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							"-5",
							style = MaterialTheme.typography.bodyLarge
						)
					}
					Spacer(modifier = Modifier.width(10.dp))
					Button(
						onClick = { if (count > 1) count-- },
						modifier = Modifier.size(40.dp),
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							"-1",
							style = MaterialTheme.typography.bodyLarge
						)
					}
					
					Spacer(modifier = Modifier.width(10.dp))
					Button(
						onClick = { count++ },
						modifier = Modifier.size(40.dp),
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							"+1",
							style = MaterialTheme.typography.bodyLarge
						)
					}
					Spacer(modifier = Modifier.width(10.dp))
					Button(
						onClick = {
							repeat(5) {
								count++
							}
						},
						modifier = Modifier.size(40.dp),
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							"+5",
							style = MaterialTheme.typography.bodyLarge
						)
					}
				}
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					
					Spacer(modifier = Modifier.width(8.dp))
					Button(
						modifier = Modifier
							.fillMaxWidth()
							.height(50.dp),
						shape = MaterialTheme.shapes.large,
						onClick = { onConfirm(count) }) {
						Text(
							"확인",
							style = MaterialTheme.typography.titleLarge
						)
					}
				}
			}
		}
	}
}