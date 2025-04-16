package com.spread.lightdelivery.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.px2Dp
import com.spread.lightdelivery.ui.theme.primaryLight
import kotlin.math.max

@Composable
fun SheetPanel(
    sheets: List<DeliverSheet>,
    modifier: Modifier,
    onSheetClick: (DeliverSheet) -> Unit
) {

    var maxWidth by remember { mutableStateOf(0) }

    LazyColumn(modifier, horizontalAlignment = Alignment.Start) {
        item {
            Text(
                modifier = Modifier.width(maxWidth.px2Dp).padding(bottom = 10.dp),
                text = "订单列表",
                fontSize = 30.sp,
                color = primaryLight,
                textAlign = TextAlign.Center
            )
        }
        items(sheets.size) { index ->
            SheetCard(sheets[index], Modifier.padding(5.dp).onGloballyPositioned {
                maxWidth = max(maxWidth, it.size.width)
            }, onSheetClick)
        }
    }

}