package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.spread.lightdelivery.data.DeliverOperator
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.ui.theme.surfaceLight

@Composable
fun MainScreen(modifier: Modifier) {

    val sheets = DeliverOperator.sheets

    var currDeliverSheet by remember { mutableStateOf<DeliverSheet?>(null) }

    Row(modifier.fillMaxWidth().background(color = surfaceLight)) {
        if (sheets.isNotEmpty()) {
            SheetPanel(sheets, Modifier.wrapContentWidth().padding(20.dp)) {
                currDeliverSheet = it
            }
            VerticalDivider(modifier = Modifier.fillMaxHeight())
        }
        currDeliverSheet?.let {
            ItemsPanel(Modifier, it)
        }
    }
}