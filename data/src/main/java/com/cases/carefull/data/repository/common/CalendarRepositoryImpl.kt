package com.cases.carefull.data.repository.common

import com.cases.carefull.domain.repository.common.CalendarRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(): CalendarRepository {

	override suspend fun getSchedulesForDate(date: LocalDate): List<String> {
		return emptyList()
	}

	override fun getDaysOfWeek(date: LocalDate): List<LocalDate> {
		val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
		return generateSequence(startOfWeek) { it.plusDays(1) }.take(7).toList()
	}

	override fun getDaysOfMonth(yearMonth: YearMonth): List<LocalDate> {
		val startOfMonth =
			yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
		val endOfMonth =
			yearMonth.atEndOfMonth().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
		return generateSequence(startOfMonth) { it.plusDays(1) }
			.takeWhile { !it.isAfter(endOfMonth) }
			.toList()
	}
}