package com.github.leon.date


import java.time.*
import java.time.DayOfWeek.*
import java.time.format.DateTimeFormatter

object DateUtils {

    private val WEEKENDS = listOf(SATURDAY, SUNDAY)
    private val WEEKDAYS = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

    fun add(date: LocalDate, workdays: Int): LocalDate {
        if (workdays < 1) {
            return date
        }

        var result = date
        var addedDays = 0
        while (addedDays < workdays) {
            result = result.plusDays(1)
            if (!(result.dayOfWeek == SATURDAY || result.dayOfWeek == SUNDAY)) {
                ++addedDays
            }
        }

        return result
    }

    fun add(date: ZonedDateTime, workdays: Int): ZonedDateTime {
        if (workdays < 1) {
            return date
        }

        var result = date
        var addedDays = 0
        while (addedDays < workdays) {
            result = result.plusDays(1)
            if (!(result.dayOfWeek == SATURDAY || result.dayOfWeek == SUNDAY)) {
                ++addedDays
            }
        }

        return result
    }

    fun isWeekend(localDate: LocalDate): Boolean {
        return WEEKENDS.contains(localDate.dayOfWeek)
    }

    fun isWeekday(localDate: LocalDate): Boolean {
        return WEEKDAYS.contains(localDate.dayOfWeek)
    }


    fun getZonedDateTime(zone: String, str: String): ZonedDateTime {
        val china = ZoneId.of(zone)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")


        return ZonedDateTime.of(LocalDateTime.parse(str, formatter), china).withZoneSameInstant(ZoneOffset.UTC)
    }

}
