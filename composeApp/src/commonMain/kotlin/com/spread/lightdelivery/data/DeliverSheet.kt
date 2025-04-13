package com.spread.lightdelivery.data

import java.util.Date

data class DeliverSheet(
    val title: String,
    val phoneNumber: String,
    val customerName: String,
    val deliverAddress: String,
    val date: Date,
    val deliverItems: List<DeliverItem>
) {
    val totalPrice: Double
        get() {
            var total = 0.0
            for (item in deliverItems) {
                val price = item.totalPrice
                if (price <= 0.0) {
                    return -1.0
                }
                total += price
            }
            return total
        }
}