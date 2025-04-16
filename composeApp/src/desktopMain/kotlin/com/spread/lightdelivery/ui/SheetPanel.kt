package com.spread.lightdelivery.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.ui.theme.primaryLight

@Composable
fun SheetPanel(
    sheets: List<DeliverSheet>,
    modifier: Modifier,
    onSheetClick: (DeliverSheet) -> Unit
) {

    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "订单列表",
                fontSize = 30.sp,
                color = primaryLight,
            )
        }
        items(sheets.size) { index ->
            SheetCard(sheets[index], Modifier.padding(5.dp), onSheetClick)
        }
    }

}