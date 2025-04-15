package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spread.lightdelivery.YMDStr
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.ui.theme.secondaryLight
import com.spread.lightdelivery.ui.theme.surfaceContainerHighLight

@Composable
fun SheetCard(sheet: DeliverSheet, modifier: Modifier) {
    Box(
        modifier = modifier.background(
            color = surfaceContainerHighLight,
            shape = RoundedCornerShape(16.dp)
        ),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.Start) {
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