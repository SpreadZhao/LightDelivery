package com.spread.lightdelivery.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spread.lightdelivery.data.DeliverSheet

@Composable
fun SheetPanel(sheets: List<DeliverSheet>, modifier: Modifier) {

    LazyColumn {
        items(sheets.size) { index ->
            SheetCard(sheets[index], modifier.padding(5.dp))
        }
    }

}