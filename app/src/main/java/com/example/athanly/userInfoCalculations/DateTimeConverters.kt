package com.example.athanly.userInfoCalculations

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Explicit String <-> java.time conversions, shared across the app (Room entity mapping today,
// ViewModel formatting later) — kept in one place so every caller round-trips the same
// ISO-8601 format instead of each layer inventing its own.

fun LocalTime.toIsoString(): String = format(DateTimeFormatter.ISO_LOCAL_TIME)

fun String.toLocalTime(): LocalTime = LocalTime.parse(this, DateTimeFormatter.ISO_LOCAL_TIME)

fun LocalDate.toIsoString(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)