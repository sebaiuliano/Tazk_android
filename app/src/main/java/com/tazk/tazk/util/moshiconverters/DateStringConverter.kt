package com.tazk.tazk.util.moshiconverters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class DateString

class DateStringConverter {
    @TypeConverter
    @SuppressLint("SimpleDateFormat")
    @FromJson
    @DateString
    fun fromJson(dateString: String): GregorianCalendar? {
        return if (dateString.isNotEmpty()) {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date: Date? = df.parse(dateString)
            val cal = GregorianCalendar()
            if (date != null) {
                cal.time = date
                cal
            } else {
                null
            }
        } else {
            null
        }
    }

    @TypeConverter
    @SuppressLint("SimpleDateFormat")
    @ToJson
    fun toJson(@DateString calendar: GregorianCalendar?): String{
        return if (calendar != null) {
            val cal: Calendar = calendar
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            df.format(cal.time)
        } else {
            ""
        }
    }
}