package com.cases.carefull.data.di

import com.cases.carefull.data.repository.CalendarRepositoryImpl
import com.cases.carefull.data.repository.ChatbotRepositoryImpl
import com.cases.carefull.data.repository.DiseaseRepositoryImpl
import com.cases.carefull.data.repository.HospitalRepositoryImpl
import com.cases.carefull.data.repository.LocationRepositoryImpl
import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.data.repository.RankingRepositoryImpl
import com.cases.carefull.data.repository.SocialRepositoryImpl
import com.cases.carefull.data.repository.UserRepositoryImpl
import com.cases.carefull.data.repository.diet.BodyStatsRepositoryImpl
import com.cases.carefull.data.repository.diet.DietRecordRepositoryImpl
import com.cases.carefull.data.repository.diet.FavoriteFoodRepositoryImpl
import com.cases.carefull.data.repository.diet.FoodSearchRepositoryImpl
import com.cases.carefull.data.repository.diet.RecentMealSearchRepositoryImpl
import com.cases.carefull.data.repository.exercise.PoseRepositoryImpl
import com.cases.carefull.data.repository.exercise.TodayWorkOutRepositoryImpl
import com.cases.carefull.data.repository.exercise.WorkOutRecordRepositoryImpl
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.ChatbotRepository
import com.cases.carefull.domain.repository.DiseaseRepository
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.repository.LocationRepository
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.repository.RankingRepository
import com.cases.carefull.domain.repository.SocialRepository
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.repository.diet.BodyStatsRepository
import com.cases.carefull.domain.repository.diet.DietRecordRepository
import com.cases.carefull.domain.repository.diet.FavoriteFoodRepository
import com.cases.carefull.domain.repository.diet.FoodSearchRepository
import com.cases.carefull.domain.repository.diet.RecentMealSearchRepository
import com.cases.carefull.domain.repository.exercise.PoseRepository
import com.cases.carefull.domain.repository.exercise.TodayWorkOutRepository
import com.cases.carefull.domain.repository.exercise.WorkOutRecordRepository
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
    abstract fun bindWorkOutRecordRepository(
        workOutRepositoryImpl: WorkOutRecordRepositoryImpl
    ): WorkOutRecordRepository

    @Binds
    @ViewModelScoped
    abstract fun bindTodayWorkOutRepository(
        todayWorkOutRepositoryImpl: TodayWorkOutRepositoryImpl
    ): TodayWorkOutRepository


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
    abstract fun bindRecentMealSearchRepository(
        recentMealSearchRepositoryImpl: RecentMealSearchRepositoryImpl
    ): RecentMealSearchRepository

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
    abstract fun bindDiseaseRepository(
        diseaseRepositoryImpl: DiseaseRepositoryImpl
    ): DiseaseRepository
    
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
