package com.spread.lightdelivery.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.lightdelivery.ui.theme.onPrimaryContainerLight
import com.spread.lightdelivery.ui.theme.primaryContainerLight
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthPicker(
    modifier: Modifier,
    currentMonth: Int,
    currentYear: Int,
    confirmButtonCLicked: (Int, Int) -> Unit
) {

    var month by remember {
        mutableStateOf(currentMonth)
    }

    var year by remember {
        mutableStateOf(currentYear)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }


    Column(modifier = modifier) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier
                    .size(35.dp)
                    .rotate(90f)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            year--
                            confirmButtonCLicked(month, year)
                        }
                    ),
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = year.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                modifier = Modifier
                    .size(35.dp)
                    .rotate(-90f)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            year++
                            confirmButtonCLicked(month, year)
                        }
                    ),
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null
            )

        }


        Card(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
        ) {

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 4
            ) {

                for (m in Calendar.JANUARY..Calendar.DECEMBER) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource,
                                onClick = {
                                    month = m
                                    confirmButtonCLicked(month, year)
                                }
                            )
                            .background(
                                color = Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        val animatedSize by animateDpAsState(
                            targetValue = if (month == m) 60.dp else 0.dp,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(animatedSize)
                                .background(
                                    color = if (month == m) primaryContainerLight else Color.Transparent,
                                    shape = CircleShape
                                )
                        )

                        Text(
                            text = "${m + 1}月",
                            color = if (month == m) onPrimaryContainerLight else Color.Unspecified,
                            fontWeight = FontWeight.Medium
                        )

                    }
                }

            }

        }

    }

}