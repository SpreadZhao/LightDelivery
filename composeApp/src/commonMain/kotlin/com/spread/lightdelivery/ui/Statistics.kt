package com.spread.lightdelivery.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.currYearAndMonth
import com.spread.lightdelivery.data.SheetViewModel
import com.spread.lightdelivery.dayOfMonthNew
import com.spread.lightdelivery.maxDaysInMonth
import com.spread.lightdelivery.monthNew
import com.spread.lightdelivery.sumTotalPrice
import com.spread.lightdelivery.ui.theme.onPrimaryContainerLight
import com.spread.lightdelivery.ui.theme.primaryContainerLight
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.yearNew
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun StatisticsDialog(onDismissRequest: () -> Unit) {

    LaunchedEffect("only once") {
        SheetViewModel.refreshSheets()
    }

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
                val config = currYearAndMonth.run {
                    remember {
                        mutableStateOf(
                            StatisticsConfig(
                                year = first,
                                month = second
                            )
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Text(
                        text = "统计",
                        fontSize = 30.sp,
                        color = primaryLight,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
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

                if (pagerState.currentPage == 3 && config.value.valid) {
                    FilledIconButton(
                        onClick = {},
                        modifier = Modifier.size(40.dp).align(Alignment.TopStart)
                            .padding(start = 5.dp, top = 5.dp)
                    ) {
                        Icon(
                            imageVector = SaveIcon,
                            contentDescription = "Print"
                        )
                    }
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
    var year: Int = 0,
    var month: Int = 0,
    val customers: MutableList<Pair<String, Boolean>> = mutableStateListOf(),
    val items: MutableList<Pair<String, Boolean>> = mutableStateListOf()
) {

    data class Result(
        val year: Int,
        val month: Int,
        val customers: List<String>,
        val items: List<String>
    )

    val result: Result
        get() = Result(
            year = year,
            month = month,
            customers = customers.filter { it.second }.map { it.first },
            items = items.filter { it.second }.map { it.first }
        )

    val valid: Boolean
        get() = year > 0
                && month in Calendar.JANUARY..Calendar.DECEMBER
                && customers.all { it.second }
                && items.all { it.second }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickPage(config: StatisticsConfig) {

    MonthPicker(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(30.dp),
        currentMonth = config.month,
        currentYear = config.year,
        confirmButtonCLicked = { month, year ->
            config.month = month
            config.year = year
        }
    )

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerPickPage(config: StatisticsConfig) {
    val displayCustomers = remember { config.customers }
    LaunchedEffect("only once") {
        SheetViewModel.customerNamesInSheet.forEach { customer ->
            val target = displayCustomers.find { it.first == customer }
            if (target == null) {
                // customer not show, make it unselected
                displayCustomers.add(Pair(customer, false))
            }
        }
    }
    FlowRow(modifier = Modifier.padding(10.dp).wrapContentSize()) {
        SelectAllChip(
            selected = displayCustomers.all { it.second },
            onSelectChange = { select ->
                for (i in displayCustomers.indices) {
                    displayCustomers[i] = Pair(displayCustomers[i].first, select)
                }
            }
        )
        for (customer in displayCustomers) {
            CustomerChip(name = customer.first, selected = customer.second, onSelectChange = {
                val index = displayCustomers.indexOf(customer)
                displayCustomers[index] = Pair(customer.first, !customer.second)
            })
        }
    }
}

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
    val displayItems = remember { config.items }
    LaunchedEffect("only once") {
        SheetViewModel.itemNamesInSheet.forEach { item ->
            val target = displayItems.find { it.first == item }
            if (target == null) {
                // item not show, make it unselected
                displayItems.add(Pair(item, false))
            }
        }
    }
    FlowRow(modifier = Modifier.padding(10.dp).wrapContentSize()) {
        SelectAllChip(
            selected = displayItems.all { it.second },
            onSelectChange = { selectAll ->
                for (i in displayItems.indices) {
                    displayItems[i] = Pair(displayItems[i].first, selectAll)
                }
            }
        )
        for (item in displayItems) {
            ItemChip(name = item.first, selected = item.second, onSelectChange = {
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
fun SelectAllChip(selected: Boolean, onSelectChange: (Boolean) -> Unit) {
    FilterChip(
        colors = FilterChipDefaults.filterChipColors(
            containerColor = primaryContainerLight,
            selectedContainerColor = primaryContainerLight
        ),
        modifier = Modifier.padding(5.dp),
        onClick = {
            onSelectChange(!selected)
        },
        label = {
            Text(text = "全选", color = onPrimaryContainerLight)
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

    // map from day of month to total price of items
    val priceMap = remember { mutableStateMapOf<Int, Double>() }

    LaunchedEffect("only once") {
        // sheets that fit date and customer
        val sheets = SheetViewModel.sheets.filter { sheet ->
            val sheetYear = sheet.date.yearNew
            val sheetMonth = sheet.date.monthNew
            if (sheetYear != result.year || sheetMonth != result.month) {
                return@filter false
            }
            val sheetCustomerName = sheet.customerName
            if (sheetCustomerName.isBlank()) {
                return@filter false
            }
            if (result.customers.find { it == sheetCustomerName } == null) {
                return@filter false
            }
            return@filter true
        }

        val maxDays = result.month.maxDaysInMonth

        for (day in 1..maxDays) {
            val sheetsOfCurrDay = sheets.filter { sheet ->
                val sheetYear = sheet.date.yearNew
                val sheetMonth = sheet.date.monthNew
                val sheetDay = sheet.date.dayOfMonthNew
                sheetYear == result.year && sheetMonth == result.month && sheetDay == day
            }
            if (sheetsOfCurrDay.isEmpty()) {
                continue
            }
            if (sheetsOfCurrDay.size > 1) {
                throw RuntimeException("${result.year}年${result.month + 1}月${day}日的表格不只一个")
            }
            val sheetOfCurrDay = sheetsOfCurrDay.first()
            var totalPrice = 0.0
            for (item in sheetOfCurrDay.deliverItems) {
                if (!item.valid || result.items.find { it == item.name } == null) {
                    continue
                }
                totalPrice += item.totalPrice
            }
            if (totalPrice > 0.0) {
                priceMap[day] = totalPrice
            }
        }

    }

    LazyColumn {
        items(priceMap.toList()) { (day, price) ->
            Text(text = "${result.year}年${result.month + 1}月${day}日: $price")
        }
        item {
            priceMap.values.sumTotalPrice().takeIf { it > 0.0 }?.let {
                Text(text = "总计: $it")
            }
        }
    }

}