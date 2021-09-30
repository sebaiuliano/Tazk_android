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
annotation class FechaString

class DateStringConverter {
    @TypeConverter
    @SuppressLint("SimpleDateFormat")
    @FromJson
    @FechaString
    fun fromJson(fecha: String): GregorianCalendar? {
        val df: DateFormat = if(fecha.length == 10){
            SimpleDateFormat("yyyy-MM-dd")
        }else{
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        }
        val date: Date? = df.parse(fecha)
        val cal = GregorianCalendar()
        if (date != null)
            cal.time = date
        else
            return null
        return cal
    }

    @TypeConverter
    @SuppressLint("SimpleDateFormat")
    @ToJson
    fun toJson(@FechaString calendar: GregorianCalendar): String{
        val cal: Calendar = calendar
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        return df.format(cal.time)
    }
}