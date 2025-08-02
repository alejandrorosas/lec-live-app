package dev.alejandrorosas.strings

import android.text.format.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDate.getReadableDateString(): String {
    val today = LocalDate.now()
    if (isEqual(today) || isEqual(today.plusDays(1)) || isEqual(today.minusDays(1))) {
        return DateUtils
            .getRelativeTimeSpanString(
                this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                LocalDate
                    .now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_WEEKDAY,
            ).toString()
    }

    return this.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
}

fun LocalDateTime.getReadableTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}
