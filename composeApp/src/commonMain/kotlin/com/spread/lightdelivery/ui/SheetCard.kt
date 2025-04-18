package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.YMDStr
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.ui.theme.secondaryDark
import com.spread.lightdelivery.ui.theme.secondaryLight
import com.spread.lightdelivery.ui.theme.surfaceContainerHighDark
import com.spread.lightdelivery.ui.theme.surfaceContainerHighLight

@Composable
fun SheetCard(
    index: Int,
    sheet: DeliverSheet,
    modifier: Modifier,
    onSheetClick: (DeliverSheet) -> Unit
) {
    Box(
        modifier = modifier.background(
            color = surfaceContainerHighLight,
            shape = RoundedCornerShape(16.dp)
        ).clickable { onSheetClick(sheet) },
    ) {
        Row {
            Box(
                modifier = Modifier.wrapContentHeight().wrapContentWidth().background(
                    color = surfaceContainerHighDark,
                    shape = RoundedCornerShape(topStart = 16.dp)
                ).padding(5.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = index.toString(),
                    fontSize = 14.sp,
                    color = secondaryDark
                )
            }
            Column(modifier = Modifier.padding(5.dp), horizontalAlignment = Alignment.Start) {
                Text(text = "客户名称：${sheet.customerName}", color = secondaryLight)
                Text(text = "送货地址：${sheet.deliverAddress}", color = secondaryLight)
                Text(
                    text = "送货日期：${sheet.date.YMDStr}",
                    color = secondaryLight
                )
                val totalPrice = sheet.totalPrice
                if (totalPrice > 0.0) {
                    Text(
                        text = "总计金额：${String.format("%.2f", sheet.totalPrice)}",
                        color = secondaryLight
                    )
                }
            }
        }

    }
}