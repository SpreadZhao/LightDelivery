package com.spread.lightdelivery.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object SheetViewModel {

    val sheets = mutableStateListOf<DeliverSheet>().apply {
        addAll(DeliverOperator.sheets)
        sortByDescending { it.date }
    }

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

}