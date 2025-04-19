package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spread.lightdelivery.data.SheetViewModel
import com.spread.lightdelivery.ui.theme.surfaceLight
import kotlinx.coroutines.launch

@Composable
fun MainScreen(modifier: Modifier) {

    val sheets = remember { SheetViewModel.sheets }

    var currDeliverSheet by remember { SheetViewModel.currDeliverSheet }
    var isNewSheet by remember { mutableStateOf(false) }

    // TODO: snackbarHostState传递过深，导致snackbar展示不出来？
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->

        Row(modifier.fillMaxWidth().padding(contentPadding).background(color = surfaceLight)) {
            SheetPanel(
                sheets,
                Modifier.widthIn(max = 500.dp).padding(20.dp),
                onSettingsResult = {
                    scope.launch {
                        when (it) {
                            SettingsResult.SaveSuccess -> snackbarHostState.showSnackbar("保存成功")
                            SettingsResult.SaveFailed -> snackbarHostState.showSnackbar("保存失败")
                            else -> {}
                        }
                    }
                },
                onSheetClick = { sheet, isNew ->
                    SheetViewModel.currDeliverSheet.value = sheet
                    SheetViewModel.unsaved.value = !sheet.valid
                    isNewSheet = isNew
                    if (!isNew) {
                        SheetViewModel.refreshSheets()
                    }
                }
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            currDeliverSheet?.let {
                // key is needed to refresh UI
                // TODO: why?
                key(it.hashCode()) {
                    ItemsPanel(Modifier, it, isNewSheet) { success ->
                        scope.launch {
                            if (success) {
                                SheetViewModel.refreshSheets()
                                snackbarHostState.showSnackbar("保存成功")
                            } else {
                                // 保存失败不刷新，让内存里的sheet还留着
                                snackbarHostState.showSnackbar("保存失败")
                            }
                        }
                    }
                }
            }
        }

    }


}