package com.spread.lightdelivery.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.spread.lightdelivery.calcTotalPrice
import java.math.BigDecimal
import java.math.RoundingMode

class DeliverItem(
    name: String,               // 产品名称
    count: Double,                 // 产品数量
    price: Double,          // 产品单价
) {

    var name by mutableStateOf(name)
    var count by mutableStateOf(count)
    var price by mutableStateOf(price)

    var errMsg: String = ""

    val valid: Boolean
        get() = !(errMsg.isNotBlank() || totalPrice <= 0.0 || name.isEmpty() || name.isBlank())

    val totalPrice: Double
        get() {
            if (count <= 0 || price <= 0.0) {
                return -1.0
            }
            return calcTotalPrice(count, price)
        }
}

fun Collection<DeliverItem>.totalPrice(): Double {
    val total = this
        .filter { it.valid }
        .sumOf { it.totalPrice }

    return BigDecimal(total)
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()
}