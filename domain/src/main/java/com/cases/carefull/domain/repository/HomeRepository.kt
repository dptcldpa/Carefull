package com.cases.carefull.domain.repository


import java.time.LocalDate
import java.time.YearMonth

interface HomeRepository {
	suspend fun getSchedulesForDate(date: LocalDate): List<String>
	fun getDaysOfWeek(date: LocalDate): List<LocalDate>
	fun getDaysOfMonth(yearMonth: YearMonth): List<LocalDate>
}