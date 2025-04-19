package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.YMDStr
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.data.SheetViewModel
import com.spread.lightdelivery.ui.theme.errorContainerLight
import com.spread.lightdelivery.ui.theme.errorLight
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.ui.theme.secondaryDark
import com.spread.lightdelivery.ui.theme.secondaryLight
import com.spread.lightdelivery.ui.theme.surfaceContainerHighDark
import com.spread.lightdelivery.ui.theme.surfaceContainerHighLight
import com.spread.lightdelivery.ui.theme.tertiaryLight

@Composable
fun SheetCard(
    index: Int,
    sheet: DeliverSheet,
    modifier: Modifier,
    onSheetClick: (DeliverSheet, Boolean) -> Unit
) {

    val surfaceColor = if (sheet.valid) surfaceContainerHighLight else errorContainerLight
    val textColor = if (sheet.valid) secondaryLight else errorLight
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.background(
            color = surfaceColor,
            shape = RoundedCornerShape(16.dp)
        ).clickable { onSheetClick(sheet, !sheet.valid) },
    ) {
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
        Column(modifier = Modifier.widthIn(max = 400.dp).padding(5.dp), horizontalAlignment = Alignment.Start) {
            Text(text = "客户名称：${sheet.customerName}", color = textColor, fontSize = 20.sp)
            Text(text = "送货地址：${sheet.deliverAddress}", color = textColor, fontSize = 20.sp)
            Text(
                text = "送货日期：${sheet.date.YMDStr}",
                color = textColor,
                fontSize = 20.sp
            )
            val totalPrice = sheet.totalPrice
            if (totalPrice > 0.0) {
                Text(
                    text = "总计金额：${String.format("%.2f", sheet.totalPrice)}",
                    color = textColor,
                    fontSize = 20.sp
                )
            }
        }

        Icon(
            modifier = Modifier.align(Alignment.CenterVertically).padding(end = 5.dp).clickable {
                showDeleteDialog = true
            },
            imageVector = Icons.Filled.Delete,
            tint = tertiaryLight,
            contentDescription = "删除",
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            sheet = sheet,
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = {
                SheetViewModel.deleteSheet(sheet)
                showDeleteDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmDialog(
    sheet: DeliverSheet,
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
                        append(sheet.customerName)
                    }
                    append("于")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(sheet.date.YMDStr)
                    }
                    append("的送货单？")
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