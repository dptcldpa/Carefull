package com.cases.carefull.features.carefullcommon.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface Route

@Serializable
sealed interface MainRoute : Route {
	
	@Serializable
	data object Splash : MainRoute
	
	@Serializable
	data object Signin : MainRoute
	
	@Serializable
	data object Home : MainRoute
}

@Serializable
sealed interface RoutineRoute : Route {
	@Serializable
	data object Exercise : RoutineRoute
	
	@Serializable
	data object Diet : RoutineRoute
	
	@Serializable
	data object SearchFood : RoutineRoute
	
	@Serializable
	data object FoodInformation : RoutineRoute
}

@Serializable
sealed interface DiagnosisRoute : Route {
	
	@Serializable
	data object ChatBotScreen : DiagnosisRoute

	@Serializable
//	data class HospitalInfoScreen(val department:String,val diagnosis:String): DiagnosisRoute
	data object HospitalInfoScreen: DiagnosisRoute

	@Serializable
	data object MedicineInfoScreen : DiagnosisRoute

	@Serializable
	data object HospitalSearchScreen : DiagnosisRoute

	@Serializable
	data object DiseaseSearchScreen : DiagnosisRoute

	@Serializable
	data object MedicineSearchScreen : DiagnosisRoute

	@Serializable
	data object MedicineDetailScreen : DiagnosisRoute
}

@Serializable
sealed interface FeedRoute : Route {
	
	@Serializable
	data object Social : FeedRoute
	
	@Serializable
	data object Ranking : FeedRoute
}

@Serializable
sealed interface MyPageRoute : Route {
	@Serializable
	data object MyPage : MyPageRoute
	
	@Serializable
	data object AccountManagement : MyPageRoute
	
	@Serializable
	data object BasalMetabolicRateMeasurement : MyPageRoute
	
	@Serializable
	data object PostWrittenManagement : MyPageRoute
}