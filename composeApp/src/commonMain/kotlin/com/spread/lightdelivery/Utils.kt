package com.spread.lightdelivery

import java.math.BigDecimal

fun calcTotalPrice(a: Int, b: Double): Double {
    return calcTotalPrice(a.toLong(), b)
}

fun calcTotalPrice(a: Long, b: Double): Double {
    return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).toDouble()
}