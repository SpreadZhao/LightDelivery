package com.spread.lightdelivery.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.data.Config
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.px2Dp
import com.spread.lightdelivery.ui.theme.primaryLight
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun SheetPanel(
    sheets: List<DeliverSheet>,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onSheetClick: (DeliverSheet) -> Unit
) {

    var maxWidth by remember { mutableStateOf(0) }

    LazyColumn(modifier, horizontalAlignment = Alignment.Start) {
        item {
            SheetPanelHeader(
                modifier = Modifier.width(maxWidth.px2Dp).padding(10.dp),
                snackbarHostState
            )
        }
        items(sheets.size) { index ->
            SheetCard(sheets[index], Modifier.padding(5.dp).onGloballyPositioned {
                maxWidth = max(maxWidth, it.size.width)
            }, onSheetClick)
        }
    }

}

@Composable
fun SheetPanelHeader(modifier: Modifier, snackbarHostState: SnackbarHostState) {

    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentHeight()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
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
    }

    val scope = rememberCoroutineScope()

    if (showSettingsDialog) {
        SettingsDialog {
            showSettingsDialog = false
            scope.launch {
                when (it) {
                    SettingsResult.SaveSuccess -> {
                        snackbarHostState.showSnackbar("保存成功")
                    }
                    SettingsResult.SaveFailed -> {
                        snackbarHostState.showSnackbar("保存失败")
                    }
                    else -> {}
                }
            }
        }
    }

}

enum class SettingsResult {
    Cancel, SaveSuccess, SaveFailed
}

@Composable
fun SettingsDialog(onDismissRequest: (SettingsResult) -> Unit) {

    var wholesaler by remember { mutableStateOf(Config.get().wholesaler ?: "") }

    val saveAction = {
        var res = SettingsResult.SaveFailed
        wholesaler.takeIf { it.isNotEmpty() }?.let {
            Config.updateWholesaler(wholesaler)
            res = SettingsResult.SaveSuccess
        }
        onDismissRequest(res)
    }

    Dialog(
        onDismissRequest = {},
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

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Text(
                        text = "设置",
                        fontSize = 30.sp,
                        color = primaryLight,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    OutlinedTextField(
                        value = wholesaler,
                        onValueChange = { wholesaler = it },
                        label = { Text("订单标题（商家的名字和电话）") }
                    )
                }

                FilledIconButton(
                    onClick = {
                        onDismissRequest(SettingsResult.Cancel)
                    },
                    modifier = Modifier.size(40.dp).align(Alignment.TopEnd)
                        .padding(top = 5.dp, end = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Exit",
                        modifier = Modifier.align(Alignment.CenterEnd).padding(10.dp)
                    )
                }

                FilledIconButton(
                    onClick = saveAction,
                    modifier = Modifier.size(40.dp).align(Alignment.TopStart)
                        .padding(top = 5.dp, start = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Save",
                        modifier = Modifier.align(Alignment.CenterEnd).padding(10.dp)
                    )
                }

            }
        }
    }
}
