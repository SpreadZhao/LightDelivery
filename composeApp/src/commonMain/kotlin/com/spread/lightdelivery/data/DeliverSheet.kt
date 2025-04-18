package com.spread.lightdelivery.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Date

class DeliverSheet(
    title: String = "",
    customerName: String = "",
    deliverAddress: String = "",
    date: Date = Date(),
    deliverItems: List<DeliverItem> = emptyList()
) {

    var title by mutableStateOf(title)
    var customerName by mutableStateOf(customerName)
    var deliverAddress by mutableStateOf(deliverAddress)
    var date by mutableStateOf(date)
    var deliverItems by mutableStateOf(deliverItems)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return this.title == (other as? DeliverSheet)?.title
                && this.customerName == other.customerName
    }

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

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + customerName.hashCode()
        result = 31 * result + deliverAddress.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + deliverItems.hashCode()
        result = 31 * result + totalPrice.hashCode()
        return result
    }
}