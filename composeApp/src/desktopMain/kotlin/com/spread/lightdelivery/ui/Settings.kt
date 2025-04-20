package com.spread.lightdelivery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.data.Config
import com.spread.lightdelivery.data.Config.Customer
import com.spread.lightdelivery.data.Config.Item
import com.spread.lightdelivery.px2Dp
import com.spread.lightdelivery.ui.theme.primaryLight

enum class SettingsResult {
    Cancel, SaveSuccess, SaveFailed
}

@Composable
fun SettingsDialog(onDismissRequest: (SettingsResult) -> Unit) {

    var wholesaler by remember { mutableStateOf(Config.get().wholesaler ?: "") }

    var headerHeight by remember { mutableStateOf(0) }

    val customerList = remember {
        mutableStateListOf<Customer>()
    }
    val itemList = remember {
        mutableStateListOf<Item>()
    }

    LaunchedEffect("only once") {
        Config.get().customers?.map { it.copy() }?.let {
            customerList.addAll(it)
        }
        Config.get().items?.map { it.copy() }?.let {
            itemList.addAll(it)
        }
    }

    val saveAction = {
        var res = SettingsResult.SaveFailed
        wholesaler.takeIf { it.isNotEmpty() }?.let {
            // 因为已经改过了Customer对象中的值，所以这里更新
            Config.updateWholesaler(wholesaler)
            Config.updateCustomers(customerList)
            Config.updateItems(itemList)
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

                Text(
                    text = "设置",
                    fontSize = 30.sp,
                    color = primaryLight,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                        .align(Alignment.TopCenter).onGloballyPositioned {
                            headerHeight = it.size.height
                        }
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            top = headerHeight.px2Dp + 30.dp,
                            end = 20.dp,
                            bottom = 20.dp
                        )
                ) {

                    OutlinedTextField(
                        value = wholesaler,
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                        onValueChange = { wholesaler = it },
                        label = { Text(text = "订单标题（商家的名字和电话）", fontSize = 16.sp) }
                    )

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomerList(Modifier.weight(1f).padding(end = 5.dp), customerList)
                        ProductList(Modifier.weight(1f).padding(start = 5.dp), itemList)
                    }
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

@Composable
fun CustomerList(modifier: Modifier, customers: List<Customer>) {
    LazyColumn(modifier) {
        itemsIndexed(customers) { index, customer ->
            CustomerItem(customer)
            if (index != customers.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 5.dp)
            }
        }
    }
}

@Composable
fun CustomerItem(customer: Customer) {
    var name by remember { mutableStateOf(customer.name) }
    var address by remember { mutableStateOf(customer.address) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        ClearableOutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                customer.name = it
            },
            label = { Text(text = "客户名称", fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        ClearableOutlinedTextField(
            value = address,
            onValueChange = {
                address = it
                customer.address = it
            },
            label = { Text(text = "客户地址", fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProductList(modifier: Modifier, products: List<Item>) {
    LazyColumn(modifier) {
        items(products) { product ->
            ProductItem(product)
        }
    }
}

@Composable
fun ProductItem(product: Item) {
    var name by remember { mutableStateOf(product.name) }

    ClearableOutlinedTextField(
        value = name,
        onValueChange = {
            name = it
            product.name = it
        },
        label = { Text(text = "产品名称", fontSize = 16.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}