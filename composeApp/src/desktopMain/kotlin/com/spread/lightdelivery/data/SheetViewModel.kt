package com.spread.lightdelivery.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object SheetViewModel {

    val sheets = mutableStateListOf<DeliverSheet>().apply {
        addAll(DeliverOperator.sheets)
        sortByDescending { it.date }
    }

    var currDeliverSheet = mutableStateOf<DeliverSheet?>(null)

    fun refreshSheets() {
        sheets.clear()
        sheets.addAll(DeliverOperator.sheets)
        sheets.sortByDescending { it.date }
    }

}