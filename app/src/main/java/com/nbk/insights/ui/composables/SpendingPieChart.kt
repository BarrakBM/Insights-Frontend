package com.nbk.insights.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nbk.insights.ui.theme.InsightsTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.nbk.insights.ui.theme.*

@Composable
fun SpendingPieChart(categories: List<SpendingCategory>) {
    Canvas(modifier = Modifier.size(160.dp)) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2.5f
        val center = Offset(size.width / 2, size.height / 2)

        var startAngle = -90f

        categories.forEach { category ->
            val sweepAngle = (category.percentage / 100f) * 360f

            drawArc(
                color = category.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }

        drawCircle(
            color = Color.White,
            radius = radius * 0.4f,
            center = center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingPieChartPreview() {
    InsightsTheme {
        val sampleCategories = listOf(
            SpendingCategory("Dining", 1140f, 30f, CategoryDining, Icons.Default.Restaurant),
            SpendingCategory("Shopping", 912f, 24f, CategoryShopping, Icons.Default.ShoppingBag),
            SpendingCategory("Transport", 608f, 16f, CategoryTransport, Icons.Default.DirectionsCar),
            SpendingCategory("Entertainment", 456f, 12f, CategoryEntertainment, Icons.Default.Movie),
            SpendingCategory("Utilities", 380f, 10f, CategoryUtilities, Icons.Default.Bolt),
            SpendingCategory("Healthcare", 304f, 8f, CategoryHealthcare, Icons.Default.LocalHospital)
        )
        SpendingPieChart(sampleCategories)
    }
}