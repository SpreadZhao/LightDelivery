package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.data.DeliverItem
import com.spread.lightdelivery.ui.theme.errorLight
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.ui.theme.secondaryLight
import com.spread.lightdelivery.ui.theme.surfaceContainerHighLight
import com.spread.lightdelivery.ui.theme.tertiaryLight

@Composable
fun DeliverItemCard(item: DeliverItem, modifier: Modifier, onItemDelete: (DeliverItem) -> Unit) {

    var showDeleteDialog by remember { mutableStateOf(false) }

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
                Text(text = "数量: ${item.count}", color = secondaryLight, fontSize = 20.sp)
                Text(text = "单价: ${item.price}", color = secondaryLight, fontSize = 20.sp)
                Text(
                    text = "总价: ${String.format("%.2f", item.totalPrice)}",
                    color = secondaryLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 5.dp)
                    .clickable {
                        showDeleteDialog = true
                    },
                imageVector = Icons.Filled.Delete,
                tint = tertiaryLight,
                contentDescription = "删除",
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            item = item,
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = {
                onItemDelete(item)
                showDeleteDialog = false
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmDialog(
    item: DeliverItem,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.widthIn(max = 1000.dp),
        icon = {
            Icon(
                imageVector = Icons.Filled.Delete,
                tint = errorLight,
                contentDescription = "Confirm Delete"
            )
        },
        title = {
            Text(
                text = "确认删除送货单",
                color = primaryLight
            )
        },
        text = {
            Text(
                text = buildAnnotatedString {
                    append("删除")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(item.name)
                    }
                    append("？")
                },
                fontSize = 20.sp,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "确定",
                    fontSize = 20.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "取消",
                    fontSize = 20.sp
                )
            }
        }
    )
}
