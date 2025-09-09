package com.cases.carefull.features.carefullcontents.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcontents.routine.diet.BmrScreen
import com.cases.carefull.features.carefullcontents.routine.diet.DietScreen
import com.cases.carefull.features.carefullcontents.routine.diet.DietSearchScreen
import com.cases.carefull.features.carefullcontents.routine.diet.FoodInformation
import com.cases.carefull.features.carefullcontents.routine.exercise.ExerciseScreen
import com.cases.carefull.features.carefullcontents.routine.exercise.WorkOutScreen


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.routineGraph(navController: NavHostController) {

	composable<RoutineRoute.ExerciseScreen> {

		ExerciseScreen(
			navController = navController
		)
	}
	composable<RoutineRoute.WorkOutScreen> {

		WorkOutScreen(
			navController = navController
		)
	}
	composable<RoutineRoute.DietScreen> {
		DietScreen(
			navController = navController
		)
	}
	composable<RoutineRoute.DietSearchScreen> {
		DietSearchScreen(
			navController = navController
		)
	}
	composable<RoutineRoute.BmrScreen> {
		BmrScreen()
	}
	composable<RoutineRoute.FoodInformation> {
		FoodInformation()
	}
}