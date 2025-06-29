package com.nbk.insights.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.CashFlowCategorizedResponse
import com.nbk.insights.ui.theme.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@Composable
fun SpendingViewAllChart(
    modifier: Modifier = Modifier,
    lastMonthData: CashFlowCategorizedResponse? = null,
    thisMonthData: CashFlowCategorizedResponse? = null
) {
    // Format for currency display
    val currencyFormat = DecimalFormat("#,##0.000")

    // Get the current or last month data for display
    val displayData = thisMonthData ?: lastMonthData

    // Extract spending data by category - you can customize this based on your needs
    val spendingByCategory = displayData?.moneyOutByCategory ?: emptyMap()
    val spendingValues = if (spendingByCategory.isNotEmpty()) {
        spendingByCategory.values.map { it.toFloat() }
    } else {
        // Fallback to sample data if no real data available
        listOf(300f, 420f, 310f, 350f, 700f, 540f, 560f, 830f, 1250f)
    }

    // Calculate totals
    val totalSpent = displayData?.moneyOut ?: BigDecimal.ZERO
    val totalIncome = displayData?.moneyIn ?: BigDecimal.ZERO
    val net = displayData?.netCashFlow

    // Get the date range for display
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val displayDate = displayData?.from?.format(dateFormatter) ?: "Current Month"

    val maxSpending = spendingValues.maxOrNull()?.times(1.2f) ?: 1400f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Monthly Spending", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NBKBlue)
                Text(displayDate, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (spendingValues.isNotEmpty()) {
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                ) {
                    val chartHeight = size.height
                    val chartWidth = size.width
                    val stepX = chartWidth / (spendingValues.size - 1).coerceAtLeast(1)
                    val maxY = maxSpending

                    val path = Path()
                    val fillPath = Path()

                    spendingValues.forEachIndexed { index, value ->
                        val x = stepX * index
                        val y = chartHeight - (value / maxY) * chartHeight
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    fillPath.addPath(path)
                    fillPath.lineTo(size.width, size.height)
                    fillPath.lineTo(0f, size.height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(NBKBlueAlpha10, Color.Transparent)
                        )
                    )

                    drawPath(
                        path = path,
                        color = NBKBlue,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    spendingValues.forEachIndexed { index, value ->
                        val x = stepX * index
                        val y = chartHeight - (value / maxY) * chartHeight
                        drawCircle(
                            color = NBKBlue,
                            radius = 6f,
                            center = Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Spent", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "KD ${currencyFormat.format(totalSpent)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NBKBlue
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Income", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "KD ${currencyFormat.format(totalIncome)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NBKBlue
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Net", fontSize = 14.sp, color = Color.Gray)
                    if (net != null) {
                        Text(
                            "KD ${currencyFormat.format(net)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (net >= BigDecimal.ZERO) Success else Error
                        )
                    }
                }
            }

            // Optional: Show spending categories
            if (spendingByCategory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Top Categories",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
                )
                spendingByCategory.entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .forEach { (category, amount) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                category,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                "KD ${currencyFormat.format(amount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = NBKBlue
                            )
                        }
                    }
            }
        }
    }
}