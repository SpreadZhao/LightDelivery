package com.spread.lightdelivery.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.spread.lightdelivery.data.Config.Customer
import java.util.Date

object SheetViewModel {

    val sheets = mutableStateListOf<DeliverSheet>().apply {
        addAll(DeliverOperator.sheets)
        sortByDescending { it.date }
    }

    val customerNamesInSheet: List<String>
        get() = sheets.map { it.customerName }

    val itemNamesInSheet: List<String>
        get() = sheets.flatMap { it.deliverItems }.map { it.name }

    var currDeliverSheet = mutableStateOf<DeliverSheet?>(null)

    var unsaved = mutableStateOf(false)

    fun refreshSheets() {
        sheets.clear()
        sheets.addAll(DeliverOperator.sheets)
        sheets.sortByDescending { it.date }
    }

    fun deleteSheet(sheet: DeliverSheet) {
        DeliverOperator.deleteSheet(sheet)
        sheets.remove(sheet)
    }

    data class SaveResult(
        var success: Boolean = false,
        var errMsg: String = ""
    )

    fun save(
        sheet: DeliverSheet,
        customerName: String,
        address: String,
        date: Date,
        items: List<DeliverItem>,
        isNew: Boolean
    ): SaveResult {
        val result = SaveResult()
        if (!writeSheetConfig(result, customerName, address)) {
            return result
        }
        val wholesaler = Config.get().wholesaler ?: run {
            result.errMsg = "请先设置你的名字（批发商）"
            return result
        }
        val fileName = DeliverSheet.getFileName(customerName, date)
        val writeSuccess = DeliverOperator.writeToFile(
            result, fileName, sheet.apply {
                this.title = wholesaler
                this.customerName = customerName
                this.deliverAddress = address
                this.date = date
                this.deliverItems = items
            }, isNew
        )
        result.success = writeSuccess
        return result
    }

    private fun writeSheetConfig(
        result: SaveResult,
        customerName: String,
        address: String
    ): Boolean {
        if (customerName.isEmpty()) {
            result.errMsg = "请输入客户名称"
            return false
        }
        if (address.isEmpty()) {
            result.errMsg = "请输入客户地址"
            return false
        }
        Config.addNewCustomer(Customer(customerName, address))
        return true
    }

}