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
import com.nbk.insights.ui.theme.*

@Composable
fun SpendingViewAllChart(modifier: Modifier = Modifier) {
    val spendingData = listOf(300f, 420f, 310f, 350f, 700f, 540f, 560f, 830f, 1250f)
    val maxSpending = 1400f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Spending Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NBKBlue)
                Text("May 2023", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            ) {
                val chartHeight = size.height
                val chartWidth = size.width
                val stepX = chartWidth / (spendingData.size - 1)
                val maxY = maxSpending

                val path = Path()
                val fillPath = Path()

                spendingData.forEachIndexed { index, value ->
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

                spendingData.forEachIndexed { index, value ->
                    val x = stepX * index
                    val y = chartHeight - (value / maxY) * chartHeight
                    drawCircle(
                        color = NBKBlue,
                        radius = 6f,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Spent", fontSize = 14.sp, color = Color.Gray)
                    Text("KD 1,230", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NBKBlue)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Income", fontSize = 14.sp, color = Color.Gray)
                    Text("KD 2,500", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NBKBlue)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Saved", fontSize = 14.sp, color = Color.Gray)
                    Text("KD 1,270", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Success)
                }
            }
        }
    }
}