package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.InsightsTheme

@Composable
fun SpendingBarChart(spendingData: List<Float>) {
    val maxValue = spendingData.maxOrNull() ?: 4000f
    val months = listOf("Oct", "Nov", "Dec")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        spendingData.forEachIndexed { index, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(value / 1000).toInt()},${((value % 1000) / 100).toInt()}00",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height((value / maxValue * 160).dp)
                        .background(Color(0xFFEF4444), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = months[index],
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingBarChartPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Spending Chart Preview")
            Spacer(modifier = Modifier.height(16.dp))
            SpendingBarChart(listOf(3500f, 3200f, 3800f))
        }
    }
}