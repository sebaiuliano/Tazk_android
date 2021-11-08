package com.tazk.tazk.util

import java.text.SimpleDateFormat
import java.util.*

object Tools {
    fun gregorianCalendarToString(gc: GregorianCalendar, format: String) : String {
        val date = Date()
        date.time = gc.timeInMillis
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }

    fun dateWithOffset(date: Date) : Date {
        val timeZoneUTC : TimeZone = TimeZone.getDefault()
        val offsetFromUTC = timeZoneUTC.getOffset(Date().time)
        return Date(date.time - offsetFromUTC)
    }
}