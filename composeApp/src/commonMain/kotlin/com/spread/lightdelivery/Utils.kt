package com.spread.lightdelivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date

/* Date */

fun String.toDate(): Date {
    return SimpleDateFormat("yyyy-MM-dd").parse(this)
}

val nowYMDStr: String
    get() = Date().YMDStr

val Date.YMDStr: String
    get() = SimpleDateFormat("yyyy-MM-dd").format(this)

fun calcTotalPrice(a: Int, b: Double): Double {
    return calcTotalPrice(a.toLong(), b)
}

fun calcTotalPrice(a: Long, b: Double): Double {
    return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).toDouble()
}

/* UI */

val Int.px2Dp: Dp
    @Composable
    get() = with(LocalDensity.current) { toDp() }

val Dp.px: Float
    @Composable
    get() = with(LocalDensity.current) { toPx() }