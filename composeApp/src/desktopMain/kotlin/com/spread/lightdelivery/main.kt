package com.spread.lightdelivery

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.spread.lightdelivery.ui.MainScreen
import com.spread.lightdelivery.ui.theme.LightDeliverTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LightDelivery",
        state = rememberWindowState(size = DpSize(1500.dp, 1000.dp))
    ) {
        LightDeliverTheme {
            MainScreen(Modifier)
        }
    }
}