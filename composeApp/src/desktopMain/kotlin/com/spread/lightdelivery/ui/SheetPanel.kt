package com.spread.lightdelivery.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.data.Config
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.data.SheetViewModel
import com.spread.lightdelivery.px
import com.spread.lightdelivery.px2Dp
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.ui.theme.secondaryLight
import kotlin.math.max

@Composable
fun SheetPanel(
    sheets: MutableList<DeliverSheet>,
    modifier: Modifier,
    onSettingsResult: (SettingsResult) -> Unit,
    onSheetClick: (DeliverSheet, Boolean) -> Unit
) {

    var maxWidth by remember { mutableStateOf(0) }

    val minWidth = 300.dp.px.toInt()

    var headerHeight by remember { mutableStateOf(0) }

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.padding(top = headerHeight.px2Dp + 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            items(sheets.size) { index ->
                SheetCard(index, sheets[index], Modifier.padding(5.dp).onGloballyPositioned {
                    maxWidth = max(maxWidth, it.size.width)
                }, onSheetClick)
            }
        }
        SheetPanelHeader(
            modifier = Modifier.width(maxWidth.coerceAtLeast(minWidth).px2Dp)
                .padding(10.dp).align(Alignment.TopCenter).onGloballyPositioned {
                    headerHeight = it.size.height
                },
            onSettingsResult = onSettingsResult,
            onAddClick = {
                val newSheet = DeliverSheet()
                sheets.add(newSheet)
                onSheetClick(newSheet, true)
            }
        )
    }


}

@Composable
fun SheetPanelHeader(
    modifier: Modifier,
    onSettingsResult: (SettingsResult) -> Unit,
    onAddClick: () -> Unit
) {

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showStatisticsDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentHeight()) {
        Text(
            modifier = Modifier.align(Alignment.Center).clickable {
                showStatisticsDialog = true
            },
            text = "订单列表",
            fontSize = 30.sp,
            color = primaryLight
        )
        FilledIconButton(
            modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
            onClick = {
                showSettingsDialog = true
            },
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings"
            )
        }
        FilledIconButton(
            modifier = Modifier.align(Alignment.CenterStart).size(40.dp),
            onClick = onAddClick,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        }
    }

    if (showStatisticsDialog) {
        StatisticsDialog {
            showStatisticsDialog = false
        }
    }

    if (showSettingsDialog) {
        SettingsDialog {
            showSettingsDialog = false
            onSettingsResult(it)
        }
    }

}

@Composable
fun StatisticsDialog(onDismissRequest: () -> Unit) {

    val statistics = remember { mutableStateMapOf<String, Double>() }

    LaunchedEffect(key1 = "only one") {
        statistics.putAll(getStatisticsResult())
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.width(1000.dp).heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    item {
                        Text(
                            text = "统计",
                            fontSize = 30.sp,
                            color = primaryLight,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    items(statistics.keys.toList()) { customerName ->
                        val price = statistics[customerName] ?: return@items
                        Text(
                            text = "$customerName: $price",
                            fontSize = 20.sp,
                            color = secondaryLight,
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 30.dp, bottom = 10.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                FilledIconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(40.dp).align(Alignment.TopEnd)
                        .padding(top = 5.dp, end = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close"
                    )
                }
            }
        }
    }
}

private fun getStatisticsResult(): Map<String, Double> {
    SheetViewModel.refreshSheets()
    val sheets = SheetViewModel.sheets
    val customerPayMap = mutableMapOf<String, Double>()
    for (sheet in sheets) {
        val price = customerPayMap[sheet.customerName] ?: 0.0
        val sheetPrice = sheet.totalPrice
        if (sheetPrice > 0.0) {
            customerPayMap[sheet.customerName] = price + sheetPrice
        }
    }
    return customerPayMap
}