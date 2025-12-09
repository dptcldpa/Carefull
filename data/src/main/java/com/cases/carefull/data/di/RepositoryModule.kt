package com.cases.carefull.data.di

import com.cases.carefull.data.repository.common.CalendarRepositoryImpl
import com.cases.carefull.data.repository.diagnosis.CareFullLocationRepositoryImpl
import com.cases.carefull.data.repository.diagnosis.ChatbotRepositoryImpl
import com.cases.carefull.data.repository.diagnosis.DiseaseRepositoryImpl
import com.cases.carefull.data.repository.diagnosis.HospitalRepositoryImpl
import com.cases.carefull.data.repository.diagnosis.MedicineRepositoryImpl
import com.cases.carefull.data.repository.feed.ranking.RankingRepositoryImpl
import com.cases.carefull.data.repository.account.UserRepositoryImpl
import com.cases.carefull.data.repository.feed.social.SocialCommentRepositoryImpl
import com.cases.carefull.data.repository.feed.social.SocialPostRepositoryImpl
import com.cases.carefull.data.repository.routine.diet.BodyStatsRepositoryImpl
import com.cases.carefull.data.repository.routine.diet.DietRecordRepositoryImpl
import com.cases.carefull.data.repository.routine.diet.FavoriteFoodRepositoryImpl
import com.cases.carefull.data.repository.routine.diet.FoodSearchRepositoryImpl
import com.cases.carefull.data.repository.routine.diet.RecentMealSearchRepositoryImpl
import com.cases.carefull.data.repository.routine.exercise.PoseRepositoryImpl
import com.cases.carefull.data.repository.routine.exercise.TodayWorkOutRepositoryImpl
import com.cases.carefull.data.repository.routine.exercise.WorkOutRecordRepositoryImpl
import com.cases.carefull.domain.repository.common.CalendarRepository
import com.cases.carefull.domain.repository.diagnosis.CareFullLocationRepository
import com.cases.carefull.domain.repository.diagnosis.ChatbotRepository
import com.cases.carefull.domain.repository.diagnosis.DiseaseRepository
import com.cases.carefull.domain.repository.diagnosis.HospitalRepository
import com.cases.carefull.domain.repository.diagnosis.MedicineRepository
import com.cases.carefull.domain.repository.feed.RankingRepository
import com.cases.carefull.domain.repository.account.UserRepository
import com.cases.carefull.domain.repository.feed.SocialCommentRepository
import com.cases.carefull.domain.repository.feed.SocialPostRepository
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository
import com.cases.carefull.domain.repository.routine.diet.DietRecordRepository
import com.cases.carefull.domain.repository.routine.diet.FavoriteFoodRepository
import com.cases.carefull.domain.repository.routine.diet.FoodSearchRepository
import com.cases.carefull.domain.repository.routine.diet.RecentMealSearchRepository
import com.cases.carefull.domain.repository.routine.exercise.PoseRepository
import com.cases.carefull.domain.repository.routine.exercise.TodayWorkOutRepository
import com.cases.carefull.domain.repository.routine.exercise.WorkOutRecordRepository
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
    abstract fun bindHospitalRepository(
        hospitalRepositoryImpl: HospitalRepositoryImpl
    ): HospitalRepository

    @Binds
    @ViewModelScoped
    abstract fun bindLocationRepository(
        careFullLocationRepositoryImpl: CareFullLocationRepositoryImpl
    ): CareFullLocationRepository

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

    @Binds
    @ViewModelScoped
    abstract fun bindSocialPostListRepository(
        socialPostRepositoryImpl: SocialPostRepositoryImpl
    ): SocialPostRepository

    @Binds
    @ViewModelScoped
    abstract fun bindSocialCommentListRepository(
        socialCommentRepositoryImpl: SocialCommentRepositoryImpl
    ): SocialCommentRepository
}
