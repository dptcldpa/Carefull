package com.cases.carefull.data.di


import com.cases.carefull.data.datasource.ChatbotDataSource
import com.cases.carefull.data.datasource.ChatbotDataSourceImpl
import com.cases.carefull.data.datasource.HospitalDataSource
import com.cases.carefull.data.datasource.HospitalDataSourceImpl
import com.cases.carefull.data.datasource.KaKaoDataSource
import com.cases.carefull.data.datasource.KakaoDataSourceImpl
import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.data.firestore.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
	
	@Binds
	@Singleton
	abstract fun bindKakaoDataSource(
		kakaoDataSourceImpl: KakaoDataSourceImpl
	): KaKaoDataSource
	
	@Binds
	@Singleton
	abstract fun bindUserDataSource(
		userDataSourceImpl: UserDataSourceImpl
	): UserDataSource

	@Binds
	@Singleton
	abstract fun bindHospitalDataSource(
		hospitalDatasourceImpl: HospitalDataSourceImpl
	): HospitalDataSource

	@Binds
	@Singleton
	abstract fun bindChatbotDataSource(
		chatbotDataSourceImpl: ChatbotDataSourceImpl
	): ChatbotDataSource
}