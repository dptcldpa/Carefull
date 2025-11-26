package com.cases.carefull.features.carefullcontents.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcontents.routine.diet.BmrRoute
import com.cases.carefull.features.carefullcontents.routine.diet.DietRoute
import com.cases.carefull.features.carefullcontents.routine.diet.DietSearchRoute
import com.cases.carefull.features.carefullcontents.routine.exercise.ExerciseRoute
import com.cases.carefull.features.carefullcontents.routine.exercise.WorkOutRoute

fun NavGraphBuilder.routineGraph(navController: NavHostController) {

    composable<RoutineRoute.ExerciseRoute> {
        ExerciseRoute(
            navController = navController
        )
    }
    composable<RoutineRoute.WorkOutRoute> {
        WorkOutRoute(
            navController = navController
        )
    }
    composable<RoutineRoute.DietRoute> {
        DietRoute(
            navController = navController
        )
    }
    composable<RoutineRoute.DietSearchRoute> {
        DietSearchRoute(
            navController = navController
        )
    }
    composable<RoutineRoute.BmrRoute> {
        BmrRoute()
    }
}
