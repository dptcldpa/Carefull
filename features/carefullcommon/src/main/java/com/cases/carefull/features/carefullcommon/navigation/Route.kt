package com.cases.carefull.features.carefullcommon.navigation

import com.cases.carefull.domain.model.exercise.ExerciseType
import kotlinx.serialization.Serializable


@Serializable
sealed interface Route

@Serializable
sealed interface MainRoute : Route {
	
	@Serializable
	data object Splash : MainRoute
	
	@Serializable
	data object SigninScreen : MainRoute
	
	@Serializable
	data object HomeScreen : MainRoute
}

@Serializable
sealed interface RoutineRoute : Route {
	@Serializable
	data object ExerciseScreen : RoutineRoute
	
	@Serializable
	data class WorkOutScreen(val exerciseType: ExerciseType, val count: Int) : RoutineRoute
	
	@Serializable
	data object DietScreen : RoutineRoute
	
	@Serializable
	data class DietSearchScreen(val mealType: String? = null) : RoutineRoute
	
	@Serializable
	data object FoodInformation : RoutineRoute
	
	@Serializable
	data object BmrScreen : RoutineRoute
}

@Serializable
sealed interface DiagnosisRoute : Route {
	
	@Serializable
	data object ChatBotScreen : DiagnosisRoute
	
	@Serializable
//	data class HospitalInfoScreen(val department:String,val diagnosis:String): DiagnosisRoute
	data object HospitalInfoScreen : DiagnosisRoute
	
	@Serializable
	data object MedicineInfoScreen: DiagnosisRoute
	
	@Serializable
	data object HospitalSearchScreen : DiagnosisRoute
	
	@Serializable
	data object DiseaseSearchScreen : DiagnosisRoute
	
	@Serializable
	data object MedicineSearchScreen : DiagnosisRoute
}

@Serializable
sealed interface FeedRoute : Route {
	
	@Serializable
	data object Social : FeedRoute
	
	@Serializable
	data object RankingScreen : FeedRoute
}

@Serializable
sealed interface MyPageRoute : Route {
	@Serializable
	data object MyPage : MyPageRoute
	
	@Serializable
	data object AccountManagement : MyPageRoute
	
	@Serializable
	data object PostWrittenManagement : MyPageRoute
}