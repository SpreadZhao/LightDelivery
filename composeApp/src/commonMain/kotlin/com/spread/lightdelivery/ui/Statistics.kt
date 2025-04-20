package com.spread.lightdelivery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spread.lightdelivery.data.Config
import com.spread.lightdelivery.data.SheetViewModel
import com.spread.lightdelivery.ui.theme.primaryLight
import com.spread.lightdelivery.ui.theme.surfaceDimLight
import com.spread.lightdelivery.ui.theme.surfaceLight
import kotlinx.coroutines.launch

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

                val pagerState = rememberPagerState(pageCount = { 4 })
                val scope = rememberCoroutineScope()

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

                    item {
                        StatisticsPager(modifier = Modifier.fillMaxWidth(), state = pagerState)
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

                val pageNames = listOf("选择日期", "选择客户", "选择产品", "查看结果")

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.padding(bottom = 10.dp).align(Alignment.BottomCenter)
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
                            label = { Text(text = name, fontSize = 15.sp) }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun StatisticsPager(modifier: Modifier, state: PagerState) {
    HorizontalPager(modifier = modifier, state = state) { pageIndex ->
        when (pageIndex) {
            0 -> DatePickPage()
            1 -> CustomerPickPage()
        }
    }

}

private fun Modifier.align(alignment: Alignment) {}

data class StatisticsConfig(
    var timeStart: Long?,
    var timeEnd: Long?,
    var customers: List<Config.Customer>,
    var items: List<Config.Item>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickPage() {
    val dateRangePickerState = rememberDateRangePickerState()

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
fun CustomerPickPage() {
    val customers = remember {
        mutableStateListOf<Config.Customer>()
    }
    LaunchedEffect("only once") {
        Config.get().customers?.let {
            customers.addAll(it)
        }
    }
    FlowRow(modifier = Modifier.padding(10.dp)) {
        for (customer in customers) {
            CustomerChip(name = customer.name)
        }
    }
}

@Composable
fun CustomerChip(name: String) {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        onClick = { selected = !selected },
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
