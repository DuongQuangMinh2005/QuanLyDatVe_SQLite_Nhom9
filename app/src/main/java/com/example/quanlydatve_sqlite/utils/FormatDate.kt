package com.example.quanlydatve_sqlite.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object FormatDate {
    fun formatNgay(input: String): String {
        val possibleFormats = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy"
        )
        for (fmt in possibleFormats) {
            try {
                val sdf = SimpleDateFormat(fmt, Locale.getDefault())
                val date = sdf.parse(input)
                if (date != null) {
                    val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    return output.format(date)
                }
            } catch (_: Exception) {}
        }
        return input
    }
    fun convertToISO8601(input: String): String {
        val possibleFormats = listOf(
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss"
        )
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (formatStr in possibleFormats) {
            try {
                val sdf = SimpleDateFormat(formatStr, Locale.getDefault())
                val date = sdf.parse(input)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (_: Exception) {}
        }
        return "" // Trả về rỗng nếu không parse được
    }
}



