package com.spread.lightdelivery.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.YMDStr
import com.spread.lightdelivery.calcTotalPrice
import com.spread.lightdelivery.data.Config
import com.spread.lightdelivery.data.Config.Customer
import com.spread.lightdelivery.data.DeliverItem
import com.spread.lightdelivery.data.DeliverOperator
import com.spread.lightdelivery.data.DeliverSheet
import com.spread.lightdelivery.data.totalPrice
import com.spread.lightdelivery.ui.theme.outlineLight
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.ui.theme.secondaryLight
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsPanel(modifier: Modifier, sheet: DeliverSheet) {
    // 使用 remember 保存一个可变的 StateList
    val items = remember { mutableStateListOf<DeliverItem>().apply { addAll(sheet.deliverItems) } }
    var showDialog by remember { mutableStateOf(false) }
    var newItem by remember { mutableStateOf(false) }
    var clickedIndex by remember { mutableStateOf(-1) }     // 点击的卡片index，或者新增卡片的index

    var currDate by remember { mutableStateOf(sheet.date) }

    val customerOptions = Config.get().customers
    var customerNameExpanded by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf(sheet.customerName) }
    var address by remember { mutableStateOf(sheet.deliverAddress) }

    Column(modifier.fillMaxWidth().padding(10.dp)) {
        OutlinedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            border = BorderStroke(1.dp, outlineLight),

            ) {
            Column(
                modifier = Modifier.widthIn(max = 1000.dp).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = customerNameExpanded,
                    onExpandedChange = { customerNameExpanded = !customerNameExpanded },
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                            .menuAnchor(MenuAnchorType.PrimaryEditable),
                        value = customerName,
                        onValueChange = { customerName = it },
                        trailingIcon = {
                            customerOptions?.run {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerNameExpanded)
                            }
                        },
                        label = { Text("客户名称") },
                    )
                    customerOptions?.let {
                        ExposedDropdownMenu(
                            modifier = Modifier.heightIn(max = 200.dp),
                            expanded = customerNameExpanded,
                            onDismissRequest = {
                                customerNameExpanded = false
                            }
                        ) {
                            it.forEach { customer ->
                                DropdownMenuItem(
                                    text = { Text(customer.name) },
                                    onClick = {
                                        customerName = customer.name
                                        address = customer.address
                                        customerNameExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("客户地址") },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val item = DeliverItem("新商品", 0, 0.0)
                items.add(item)
                showDialog = true
                newItem = true
                clickedIndex = items.lastIndex
            }, modifier = Modifier.padding(8.dp).width(120.dp)) {
                Text("添加项目")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currDate.YMDStr,
                    color = secondaryLight
                )
                VerticalDivider(
                    modifier = Modifier.heightIn(max = 15.dp).padding(horizontal = 5.dp)
                )
                Text(
                    text = "订单总价：${items.totalPrice()}元",
                    color = primaryLight,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(onClick = {
                writeSheetConfig(customerName, address)
                val wholesaler = Config.get().wholesaler ?: return@Button
                val fileName = "${customerName}_${currDate.YMDStr}.xlsx"
                DeliverOperator.writeToFile(
                    fileName, DeliverSheet(
                        title = wholesaler,
                        customerName = customerName,
                        deliverAddress = address,
                        date = Date(),
                        deliverItems = items
                    )
                )
            }, modifier = Modifier.padding(8.dp).width(120.dp)) {
                Text("保存")
            }
        }
        if (items.isEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f).widthIn(max = 1000.dp).padding(20.dp)) {
                items(items.size) { index ->
                    DeliverItemCard(
                        items[index],
                        Modifier.fillMaxWidth().padding(5.dp).clickable {
                            showDialog = true
                            clickedIndex = index
                        })
                }
            }
        } else {
            OutlinedCard(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                border = BorderStroke(1.dp, outlineLight),
            ) {
                // 列表部分
                LazyColumn(modifier = Modifier.weight(1f).widthIn(max = 1000.dp).padding(20.dp)) {
                    items(items.size) { index ->
                        DeliverItemCard(
                            items[index],
                            Modifier.fillMaxWidth().padding(5.dp).clickable {
                                showDialog = true
                                clickedIndex = index
                            })
                    }
                }
            }
        }


    }

    if (showDialog) {
        items.getOrNull(clickedIndex)?.let {
            ModifyItemDialog(it) {
                showDialog = false
                if (newItem && !it.valid) {
                    items.remove(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyItemDialog(item: DeliverItem, onDismissRequest: () -> Unit) {
    var name by remember { mutableStateOf(item.name) }
    var count by remember { mutableStateOf(if (item.count == 0) "" else item.count.toString()) }
    var price by remember { mutableStateOf(if (item.price == 0.0) "" else item.price.toString()) }
    var totalPrice by remember { mutableStateOf(item.totalPrice) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var nameExpanded by remember { mutableStateOf(false) }
    val options = Config.get().items?.map { it.name }
    errorMsg = checkValid(name, count, price)
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.width(400.dp).wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(50.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = name,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
                ExposedDropdownMenuBox(
                    expanded = nameExpanded,
                    onExpandedChange = {
                        nameExpanded = !nameExpanded
                    },
                ) {
                    OutlinedTextField(
                        value = name,
                        placeholder = {
                            Text(item.name)
                        },
                        singleLine = true,
                        onValueChange = {
                            name = it
                            errorMsg = checkValid(name, count, price)
                        },
                        trailingIcon = {
                            options?.run {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = nameExpanded)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                            .menuAnchor(MenuAnchorType.PrimaryEditable),
                        label = { Text("产品名称") },
                    )
                    options?.let {
                        ExposedDropdownMenu(
                            modifier = Modifier.heightIn(max = 200.dp),
                            expanded = nameExpanded,
                            onDismissRequest = {
                                nameExpanded = false
                            },
                        ) {
                            it.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        name = selectionOption
                                        nameExpanded = false
                                        errorMsg = checkValid(name, count, price)
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                }

                OutlinedTextField(
                    value = count,
                    placeholder = {
                        Text(item.count.toString())
                    },
                    singleLine = true,
                    onValueChange = {
                        count = it
                        val newTotalPrice = try {
                            calcTotalPrice(count.toLong(), price.toDouble())
                        } catch (e: Exception) {
                            -1.0
                        }
                        if (newTotalPrice >= 0) {
                            totalPrice = newTotalPrice
                        }
                        errorMsg = checkValid(name, count, price)
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    label = { Text("数量") },
                )
                OutlinedTextField(
                    value = price,
                    placeholder = {
                        Text(item.price.toString())
                    },
                    singleLine = true,
                    onValueChange = {
                        price = it
                        val newTotalPrice = try {
                            calcTotalPrice(count.toLong(), price.toDouble())
                        } catch (e: Exception) {
                            -1.0
                        }
                        if (newTotalPrice >= 0) {
                            totalPrice = newTotalPrice
                        }
                        errorMsg = checkValid(name, count, price)
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    label = { Text("单价") },
                )
                val msg = errorMsg
                val valid = msg.isNullOrEmpty()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End).padding(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = if (valid) Icons.Default.Info else Icons.Default.Warning,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (valid) "实时总价：${totalPrice}" else msg,
                        color = if (valid) Color.Unspecified else Color.Red
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismissRequest
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            errorMsg = checkValid(name, count, price)
                            if (errorMsg == null && tryUpdate(item, name, count, price)) {
                                onDismissRequest()
                            }
                        }
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}

private fun writeSheetConfig(customerName: String, address: String) {
    if (customerName.isEmpty() || address.isEmpty()) {
        return
    }
    Config.addNewCustomer(Customer(customerName, address))
}

private fun checkValid(name: String, count: String, price: String): String? {
    if (name.isEmpty()) {
        return "产品名称为空"
    }
    if (count.isEmpty()) {
        return "数量为空"
    }
    if (count.toUIntOrNull() == null) {
        return "数量格式不正确"
    }
    val c = count.toIntOrNull() ?: return "数量格式不正确"
    if (c <= 0) {
        return "数量必须大于0"
    }
    if (price.isEmpty()) {
        return "单价为空"
    }
    val p = price.toDoubleOrNull() ?: return "单价格式不正确"
    if (p <= 0.0) {
        return "单价必须大于0"
    }
    return null
}

private fun tryUpdate(
    item: DeliverItem,
    name: String,
    count: String,
    price: String
): Boolean {
    if (name.isEmpty() || count.isEmpty() || price.isEmpty()) {
        return false
    }
    try {
        val c = count.toInt()
        val p = price.toDouble()
        if (c > 0 && p >= 0.0) {
            item.name = name
            item.count = c
            item.price = p
            Config.addNewItem(Config.Item(item.name))
            return true
        }
        return false
    } catch (e: Exception) {
        return false
    }
}