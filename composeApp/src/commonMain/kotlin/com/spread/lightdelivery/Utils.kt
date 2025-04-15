package com.spread.lightdelivery

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date

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