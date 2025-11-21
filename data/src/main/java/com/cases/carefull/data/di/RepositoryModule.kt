package com.cases.carefull.data.di

import com.cases.carefull.data.repository.CalendarRepositoryImpl
import com.cases.carefull.data.repository.ChatbotRepositoryImpl
import com.cases.carefull.data.repository.ExerciseRepositoryImpl
import com.cases.carefull.data.repository.HospitalRepositoryImpl
import com.cases.carefull.data.repository.LocationRepositoryImpl
import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.data.repository.PoseRepositoryImpl
import com.cases.carefull.data.repository.RankingRepositoryImpl
import com.cases.carefull.data.repository.SocialRepositoryImpl
import com.cases.carefull.data.repository.UserRepositoryImpl
import com.cases.carefull.data.repository.diet.BodyStatsRepositoryImpl
import com.cases.carefull.data.repository.diet.DietRecordRepositoryImpl
import com.cases.carefull.data.repository.diet.DietSearchRecordRepositoryImpl
import com.cases.carefull.data.repository.diet.FavoriteFoodRepositoryImpl
import com.cases.carefull.data.repository.diet.FoodSearchRepositoryImpl
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.ChatbotRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.repository.LocationRepository
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.repository.PoseRepository
import com.cases.carefull.domain.repository.RankingRepository
import com.cases.carefull.domain.repository.SocialRepository
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.repository.diet.BodyStatsRepository
import com.cases.carefull.domain.repository.diet.DietRecordRepository
import com.cases.carefull.domain.repository.diet.DietSearchRecordRepository
import com.cases.carefull.domain.repository.diet.FavoriteFoodRepository
import com.cases.carefull.domain.repository.diet.FoodSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindRankingRepository(
        rankingRepositoryImpl: RankingRepositoryImpl
    ): RankingRepository

    @Binds
    @ViewModelScoped
    abstract fun bindExerciserRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository


    @Binds
    @ViewModelScoped
    abstract fun bindBodyStatsRepository(
        bodyStatsRepositoryImpl: BodyStatsRepositoryImpl
    ): BodyStatsRepository


    @Binds
    @ViewModelScoped
    abstract fun bindDietRecordRepository(
        dietRecordRepositoryImpl: DietRecordRepositoryImpl
    ): DietRecordRepository

    @Binds
    @ViewModelScoped
    abstract fun bindDietSearchRecordRepository(
        dietSearchRecordRepositoryImpl: DietSearchRecordRepositoryImpl
    ): DietSearchRecordRepository

    @Binds
    @ViewModelScoped
    abstract fun bindFavoriteDietRepository(
        favoriteFoodRepositoryImpl: FavoriteFoodRepositoryImpl
    ): FavoriteFoodRepository

    @Binds
    @ViewModelScoped
    abstract fun bindFoodSearchRepository(
        foodSearchRepositoryImpl: FoodSearchRepositoryImpl
    ): FoodSearchRepository

    @Binds
    @ViewModelScoped
    abstract fun bindCalendarRepository(
        calendarRepositoryImpl: CalendarRepositoryImpl
    ): CalendarRepository

    @Binds
    @ViewModelScoped
    abstract fun bindMedicineRepository(
        medicineRepositoryImpl: MedicineRepositoryImpl
    ): MedicineRepository

    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @ViewModelScoped
    abstract fun bindSocialRepository(
        socialRepositoryImpl: SocialRepositoryImpl
    ): SocialRepository

    @Binds
    @ViewModelScoped
    abstract fun bindHospitalRepository(
        hospitalRepositoryImpl: HospitalRepositoryImpl
    ): HospitalRepository

    @Binds
    @ViewModelScoped
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @ViewModelScoped
    abstract fun bindChatbotRepository(
        chatbotRepositoryImpl: ChatbotRepositoryImpl
    ): ChatbotRepository

    @Binds
    @ViewModelScoped
    abstract fun bindPoseRepository(
        poseRepositoryImpl: PoseRepositoryImpl
    ): PoseRepository
}