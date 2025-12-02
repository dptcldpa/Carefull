package com.cases.carefull.features.carefullcontents.routine.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.domain.model.routine.exercise.ExerciseState
import com.cases.carefull.domain.model.routine.exercise.ExerciseStatistics
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme

@Composable
fun ExerciseRoute(
    viewModel: ExerciseViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val todayExercise = uiState.dailyExercise.firstOrNull()
    ExerciseScreen(
        uiState = uiState,
        todayExercise = todayExercise,
        onClickExercise = { exerciseUiModel ->
            navController.navigate(
                RoutineRoute.WorkOutRoute(
                    exerciseType = exerciseUiModel.type
                )
            )
        }
    )
}

@Composable
fun ExerciseScreen(
    uiState: ExerciseUiState,
    todayExercise: ExerciseType?,
    onClickExercise: (ExerciseUiModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.dailyExercise.isNotEmpty()) {
            val todayExerciseType = uiState.dailyExercise.first()
            val exerciseName = todayExerciseType.type
            Text(
                stringResource(R.string.workout_title_format, exerciseName),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                textAlign = TextAlign.Start
            )
        } else {
            Text(
                stringResource(R.string.error_fetch_daily_workout_failed),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                textAlign = TextAlign.Start
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = uiState.exerciseList,
                key = { it.type.name }
            ) { exerciseUiModel ->
                val isTodayExercise = exerciseUiModel.type == todayExercise
                ExerciseCard(
                    uiModel = exerciseUiModel,
                    isTodayExercise = isTodayExercise,
                    onClick = {
                        onClickExercise(exerciseUiModel)
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
    isTodayExercise: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = uiModel.type.type
    val description = stringResource(uiModel.type.descriptionResId)

    val cardModifier = if (isTodayExercise) {
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                Modifier.border(
                    BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                    RoundedCornerShape(16.dp)
                )
            )
    } else {
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    }

    Card(
        onClick = onClick,
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = uiModel.imageResId),
                contentDescription = stringResource(R.string.common_image_format, uiModel.type),
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(
                            R.string.workout_stats_today_format,
                            uiModel.dailyCount
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(
                            R.string.workout_stats_weekly_format,
                            uiModel.weeklyCount
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(
                            R.string.workout_stats_total_format,
                            uiModel.totalCount
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseScreenPreview() {
    val fakeExerciseList = ExerciseType.entries.map { exerciseType ->
        ExerciseStatistics(
            type = exerciseType,
            totalCount = 152,
            weeklyCount = 35,
            dailyCount = if (exerciseType == ExerciseType.DUMBBELL_CURL) 10 else 0
        ).toUiModel()
    }
    val fakeTodayExercise = ExerciseType.DUMBBELL_CURL
    val fakeUiState = ExerciseUiState(
        count = 10,
        userPose = ExerciseState.NONE,
        detectedPose = null,
        isLoading = false,
        isError = false,
        showDialog = false,
        selectedExercise = null,
        exerciseList = fakeExerciseList,
        dailyExercise = ExerciseType.entries,
        completedDailyExerciseDates = emptySet(),
    )

    CarefullTheme {
        ExerciseScreen(
            uiState = fakeUiState,
            todayExercise = fakeTodayExercise,
            onClickExercise = {}
        )
    }
}
