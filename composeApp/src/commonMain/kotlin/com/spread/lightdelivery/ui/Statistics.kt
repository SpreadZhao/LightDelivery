package com.spread.lightdelivery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.data.Config
import com.spread.lightdelivery.data.SheetViewModel
import com.spread.lightdelivery.ui.theme.primaryLight
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Composable
fun StatisticsDialog(onDismissRequest: () -> Unit) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.width(1000.dp).wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.size(650.dp)) {

                val pagerState = rememberPagerState(pageCount = { 4 })
                val scope = rememberCoroutineScope()

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Text(
                        text = "统计",
                        fontSize = 30.sp,
                        color = primaryLight,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                    val config = rememberUpdatedState(StatisticsConfig())
                    StatisticsPager(
                        modifier = Modifier.fillMaxWidth(),
                        state = pagerState,
                        config = config.value
                    )
                    val pageNames = listOf("选择日期", "选择客户", "选择产品", "查看结果")
                    Spacer(modifier = Modifier.weight(1f))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(bottom = 10.dp).wrapContentSize().align(
                            Alignment.CenterHorizontally
                        ).padding(top = 10.dp)
                    ) {
                        pageNames.forEachIndexed { index, name ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = pageNames.size
                                ),
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                selected = pagerState.currentPage == index,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                icon = {},
                                label = { Text(text = name, fontSize = 15.sp) }
                            )
                        }
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

@Composable
fun StatisticsPager(modifier: Modifier, state: PagerState, config: StatisticsConfig) {
    HorizontalPager(modifier = modifier, state = state) { pageIndex ->
        when (pageIndex) {
            0 -> DatePickPage(config)
            1 -> CustomerPickPage(config)
            2 -> ItemPickPage(config)
            3 -> ResultPage(config.result)
        }
    }

}

data class StatisticsConfig(
    var timeStart: Long? = null,
    var timeEnd: Long? = null,
    var customers: MutableList<Pair<Config.Customer, Boolean>> = mutableStateListOf(),
    var items: MutableList<Pair<Config.Item, Boolean>> = mutableStateListOf()
) {

    data class Result(
        val timeStart: Long,
        val timeEnd: Long,
        val customers: List<Config.Customer>,
        val items: List<Config.Item>
    )

    val result: Result
        get() = Result(
            timeStart = timeStart ?: 0L,
            timeEnd = timeEnd ?: 0L,
            customers = customers.filter { it.second }.map { it.first },
            items = items.filter { it.second }.map { it.first }
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickPage(config: StatisticsConfig) {
    val dateRangePickerState = rememberDateRangePickerState()

    LaunchedEffect(
        dateRangePickerState.selectedStartDateMillis,
        dateRangePickerState.selectedEndDateMillis
    ) {
        config.timeStart = dateRangePickerState.selectedStartDateMillis
        config.timeEnd = dateRangePickerState.selectedEndDateMillis
    }

    DateRangePicker(
        state = dateRangePickerState,
        showModeToggle = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerPickPage(config: StatisticsConfig) {
    val displayCustomers = config.customers
    LaunchedEffect("only once") {
        Config.get().customers?.forEach { customer ->
            val target = displayCustomers.find { it.first.name == customer.name }
            if (target == null) {
                // customer not show, make it unselected
                displayCustomers.add(Pair(customer, false))
            }
        }
    }
    FlowRow(modifier = Modifier.padding(10.dp).wrapContentSize()) {
        for (customer in displayCustomers) {
            CustomerChip(name = customer.first.name, selected = customer.second, onSelectChange = {
                val index = displayCustomers.indexOf(customer)
                displayCustomers[index] = Pair(customer.first, !customer.second)
            })
        }
    }
}

// TODO: 顾客和产品不能从config里取，要从sheet里取。这里的配置都得改

@Composable
fun CustomerChip(name: String, selected: Boolean, onSelectChange: (Boolean) -> Unit) {
    FilterChip(
        modifier = Modifier.padding(5.dp),
        onClick = {
            onSelectChange(!selected)
        },
        label = {
            Text(name)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemPickPage(config: StatisticsConfig) {
    val displayItems = config.items
    LaunchedEffect("only once") {
        Config.get().items?.forEach { item ->
            val target = displayItems.find { it.first.name == item.name }
            if (target == null) {
                // item not show, make it unselected
                displayItems.add(Pair(item, false))
            }
        }
    }
    FlowRow(modifier = Modifier.padding(10.dp).wrapContentSize()) {
        for (item in displayItems) {
            ItemChip(name = item.first.name, selected = item.second, onSelectChange = {
                val index = displayItems.indexOf(item)
                displayItems[index] = Pair(item.first, !item.second)
            })
        }
    }
}

@Composable
fun ItemChip(name: String, selected: Boolean, onSelectChange: (Boolean) -> Unit) {
    FilterChip(
        modifier = Modifier.padding(5.dp),
        onClick = {
            onSelectChange(!selected)
        },
        label = {
            Text(name)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@Composable
fun ResultPage(result: StatisticsConfig.Result) {

    // map from customer name to total price of items
    val priceMap = remember { mutableStateMapOf<String, Double>() }

    LaunchedEffect("only once") {
        SheetViewModel.refreshSheets()
        // sheets that fit date and customer
        val sheets = SheetViewModel.sheets.filter { sheet ->
            val sheetTimestamp = sheet.date.time
            if (!dateValid(sheetTimestamp, result.timeStart, result.timeEnd)) {
                return@filter false
            }
            val sheetCustomerName = sheet.customerName
            if (sheetCustomerName.isBlank()) {
                return@filter false
            }
            if (result.customers.find { it.name == sheetCustomerName } == null) {
                return@filter false
            }
            return@filter true
        }

        for (sheet in sheets) {
            val items = sheet.deliverItems
            for (item in items) {
                val itemName = item.name
                val customerName = sheet.customerName
                if (result.items.find { it.name == itemName } == null) {
                    continue
                }
                val price = priceMap[customerName] ?: 0.0
                val itemPrice = item.totalPrice.takeIf { it > 0.0 }
                if (itemPrice != null) {
                    priceMap[customerName] = price + itemPrice
                }
            }
        }

    }

    LazyColumn {
        items(priceMap.toList()) { (customerName, price) ->
            Text(text = "$customerName: $price")
        }
    }

}


fun dateValid(time: Long, start: Long, end: Long): Boolean {
    val date = Date(time)

    val startDate = Date(start)
    val endDate = Date(end)

    // Get the start and end of the day for startDate and endDate
    val startOfDay = startDate.toStartOfDay()
    val endOfDay = endDate.toEndOfDay()

    return date >= startOfDay && date <= endOfDay
}


// Extension functions to get the start and end of the day for a given Date
fun Date.toStartOfDay(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@toStartOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

fun Date.toEndOfDay(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@toEndOfDay
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.time
}