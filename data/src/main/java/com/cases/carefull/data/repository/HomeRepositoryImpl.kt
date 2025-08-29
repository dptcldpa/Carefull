package com.cases.carefull.data.repository


import com.cases.carefull.domain.repository.HomeRepository
import java.time.LocalDate

class HomeRepositoryImpl : HomeRepository {
	override suspend fun getSchedulesForDate(date: LocalDate): List<String> {
		return emptyList()
	}
}