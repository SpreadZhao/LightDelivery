package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spread.lightdelivery.data.DeliverOperator
import com.spread.lightdelivery.ui.theme.surfaceLight

@Composable
fun MainScreen(modifier: Modifier) {

    val sheet = DeliverOperator.readFromFile("xianfenglu.xlsx").getOrNull(0)
    val items = sheet?.deliverItems

    Row(modifier.background(color = surfaceLight)) {
        if (sheet != null) {
            SheetPanel(listOf(sheet), Modifier)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight().padding(horizontal = 10.dp))
        if (items != null) {
            ItemsPanel(Modifier)
        }
    }
}