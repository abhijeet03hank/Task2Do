package com.hank.task2do.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {
         fun dateToString(calendardate: Calendar, format: String): String {
            //simple date formatter
            val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

            //return the formatted date string
            return dateFormatter.format(calendardate.time)
        }

         fun StringToDate(dateStr: String, format: String): Date {
            val sdformat = SimpleDateFormat(format)
            val date = sdformat.parse(dateStr)
            val cal = Calendar.getInstance()
            cal.setTime(date)
            return cal.time
        }
    }


}