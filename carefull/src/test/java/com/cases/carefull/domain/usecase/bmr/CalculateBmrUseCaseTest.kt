package com.cases.carefull.domain.usecase.bmr

import com.cases.carefull.domain.model.diet.BmrMovementLevel
import com.cases.carefull.domain.model.diet.Gender
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateBmrUseCaseTest {
    private lateinit var calculateBmrUseCase: CalculateBmrUseCase

    @Before
    fun setUp() {
        calculateBmrUseCase = CalculateBmrUseCase()
    }

    @Test
    fun `정상입력`() {
        val result = calculateBmrUseCase(
            gender = Gender.MALE,
            height = 180,
            weight = 80,
            age = 25,
            movementLevel = BmrMovementLevel.LIGHT
        )

        val expectedBmr = 1805
        val expectedTdee = 2481 // expectedBmr * 1.375

        assertEquals("BMR 계산 오류", expectedBmr, result.bmr)
        assertEquals("TDEE 계산 오류", expectedTdee, result.tdee)
    }

    @Test
    fun `비정상입력(신장 0cm)`() {
        val result = calculateBmrUseCase(
            gender = Gender.MALE,
            height = 0,
            weight = 70,
            age = 20,
            movementLevel = BmrMovementLevel.EXTREME
        )

        assertEquals(0, result.bmr)
        assertEquals(0, result.tdee)
    }
}