package com.mukmuk.todori.util

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDate as KxLocalDate
import java.time.LocalDate as JtLocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun KxLocalDate.toJavaLocalDate(): JtLocalDate =
    JtLocalDate.of(this.year, this.monthNumber, this.dayOfMonth)
