package com.spread.lightdelivery

import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.spread.lightdelivery.ui.MainScreen
import com.spread.lightdelivery.ui.theme.LightDeliverTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LightDelivery",
    ) {
        LightDeliverTheme {
            MainScreen(Modifier)
        }
    }
}