package com.spread.lightdelivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/* Date */

val INVALID_DATE = Date(0)

fun String.toDate(): Date {
    return SimpleDateFormat("yyyy-MM-dd").parse(this)
}

val nowYMDStr: String
    get() = Date().YMDStr

val Date.YMDStr: String
    get() = SimpleDateFormat("yyyy-MM-dd").format(this)


val Date.dayOfMonthNew: Int
    get() {
        val cal = Calendar.getInstance()
        cal.time = this
        return cal.get(Calendar.DAY_OF_MONTH)
    }

val Date.monthNew: Int
    get() {
        val cal = Calendar.getInstance()
        cal.time = this
        return cal.get(Calendar.MONTH)
    }

val Date.yearNew: Int
    get() {
        val cal = Calendar.getInstance()
        cal.time = this
        return cal.get(Calendar.YEAR)
    }

val Int.maxDaysInMonth: Int
    get() {
        try {
            val cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, this)
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        } catch (_: Exception) {
            return 0
        }
    }

val currYearAndMonth: Pair<Int, Int>
    get() {
        val cal = Calendar.getInstance()
        cal.time = Date()
        return cal.get(Calendar.YEAR) to cal.get(Calendar.MONTH)
    }

fun calcTotalPrice(a: Double, b: Double): Double {
    return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).toDouble()
}

fun Iterable<Double>.sumTotalPrice(): Double {
    var sum = BigDecimal.valueOf(0.0)
    for (i in this) {
        sum = sum.add(BigDecimal.valueOf(i))
    }
    return sum.toDouble()
}

/* UI */

val Int.px2Dp: Dp
    @Composable
    get() = with(LocalDensity.current) { toDp() }

val Dp.px: Float
    @Composable
    get() = with(LocalDensity.current) { toPx() }