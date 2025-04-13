package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.data.DeliverItem
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.ui.theme.secondaryLight
import com.spread.lightdelivery.ui.theme.surfaceContainerHighLight

@Composable
fun DeliverItemCard(item: DeliverItem, modifier: Modifier) {
    Box(
        modifier = modifier.background(
            color = surfaceContainerHighLight,
            shape = RoundedCornerShape(16.dp)
        ),
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = primaryLight
            )
            VerticalDivider(modifier = Modifier.height(40.dp).padding(horizontal = 10.dp))
            Column {
                Text(text = "数量: ${item.count}", color = secondaryLight)
                Text(text = "单价: ${item.price}", color = secondaryLight)
                Text(
                    text = "总价: ${String.format("%.2f", item.totalPrice)}",
                    color = secondaryLight
                )
            }
        }
    }
}