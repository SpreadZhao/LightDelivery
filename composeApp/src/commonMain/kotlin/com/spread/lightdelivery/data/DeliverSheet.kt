package com.spread.lightdelivery.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.spread.lightdelivery.INVALID_DATE
import com.spread.lightdelivery.YMDStr
import java.util.Date

class DeliverSheet(
    title: String = "",
    customerName: String = "",
    deliverAddress: String = "",
    date: Date = Date(),
    deliverItems: List<DeliverItem> = emptyList()
) {

    companion object {
        fun getFileName(customerName: String, date: Date): String {
            return "${customerName}_${date.YMDStr}.xlsx"
        }
    }

    val fileName: String
        get() = getFileName(customerName, date)

    var fromLocal by mutableStateOf(false)

    var title by mutableStateOf(title)
    var customerName by mutableStateOf(customerName)
    var deliverAddress by mutableStateOf(deliverAddress)
    var date by mutableStateOf(date)
    var deliverItems by mutableStateOf(deliverItems)

    /**
     * Compose会用equals判断是否相等，如果相等是不会刷新UI的。
     * 所以这里必须要保证批发商（title），顾客名字和日期都一样，
     * 才一定是同一个单子。
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return this.title == (other as? DeliverSheet)?.title
                && this.customerName == other.customerName
                && this.date == other.date
    }

    /**
     * 所有信息正确，并且已经被保存到了文件中
     */
    val valid: Boolean
        get() {
            if (title.isEmpty()) {
                return false
            }
            if (customerName.isEmpty()) {
                return false
            }
            if (deliverAddress.isEmpty()) {
                return false
            }
            if (date == INVALID_DATE) {
                return false
            }
            for (item in deliverItems) {
                if (!item.valid) {
                    return false
                }
            }
            return DeliverOperator.fileExists(fileName) && fromLocal
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