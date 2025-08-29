package com.cases.carefull.domain.repository


import java.time.LocalDate

interface HomeRepository {
	suspend fun getSchedulesForDate(date: LocalDate): List<String>
}