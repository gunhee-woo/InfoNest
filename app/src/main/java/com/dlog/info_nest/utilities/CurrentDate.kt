package com.dlog.info_nest.utilities

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun currentDate(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
    return current.format(formatter);
}

fun currentDateLong(date : String) : Long {
    val formatter = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
    return formatter.parse(date).time;
}