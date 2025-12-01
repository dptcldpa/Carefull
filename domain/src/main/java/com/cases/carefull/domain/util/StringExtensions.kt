package com.cases.carefull.domain.util

fun String?.toSafeInt(): Int {
    if (this.isNullOrBlank()) return 0
    return this.toDoubleOrNull()?.toInt() ?: 0
}