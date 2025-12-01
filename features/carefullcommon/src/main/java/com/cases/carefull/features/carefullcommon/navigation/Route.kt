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
    data object ExerciseRoute : RoutineRoute

    @Serializable
    data class WorkOutRoute(val exerciseType: ExerciseType) : RoutineRoute

    @Serializable
    data object DietRoute : RoutineRoute

    @Serializable
    data class DietSearchRoute(val mealType: String? = null, val date: String? = null) :
        RoutineRoute

    @Serializable
    data object BmrRoute : RoutineRoute
}

@Serializable
sealed interface DiagnosisRoute : Route {
	
	@Serializable
	object ChatBotScreen : DiagnosisRoute
	
	@Serializable
	data class HospitalListScreen(
		val department: String,
		val diagnosis: String
	) : DiagnosisRoute
	
	@Serializable
	data object MedicineInfoScreen: DiagnosisRoute
	
	@Serializable
	data object HospitalSearchScreen : DiagnosisRoute
	
	@Serializable
	data object DiseaseSearchScreen : DiagnosisRoute
	
	@Serializable
	data object MedicineSearchScreen : DiagnosisRoute

    @Serializable
    data class DiseaseDetailScreen(val contentSn: String) : DiagnosisRoute
}

@Serializable
sealed interface FeedRoute : Route {

    @Serializable
    data object SocialListScreen : FeedRoute

    @Serializable
    data class PostDetailScreen(val postId: String) : FeedRoute

    @Serializable
    data class CreatePostScreen(val postId: String? = null) : FeedRoute

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