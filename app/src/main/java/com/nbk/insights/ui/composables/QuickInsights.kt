package com.nbk.insights.ui.composables

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nbk.insights.ui.theme.*
import com.nbk.insights.ui.screens.drawColoredShadow
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.RecommendationsViewModel

@Composable
fun QuickInsights() {
    val activity = LocalActivity.current as ComponentActivity
    val viewmodel: RecommendationsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideRecommendationsViewModelFactory(activity) }
    )

    LaunchedEffect(Unit) {
        if(viewmodel.quickInsights.value == null){
            viewmodel.fetchQuickInsights()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Quick Insights",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        val quickInsights = viewmodel.quickInsights.value
        val isLoading = viewmodel.isLoading.value

        if (isLoading && quickInsights == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PrimaryBlue
                )
            }
        } else if (quickInsights != null) {
            val insights = buildList {
                if (quickInsights.spendingComparedToLastMonth.isNotBlank()) {
                    add(
                        InsightData(
                            icon = if (quickInsights.spendingComparedToLastMonth.contains("less", ignoreCase = true))
                                Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                            title = "Monthly Spending Update",
                            description = quickInsights.spendingComparedToLastMonth,
                            color = if (quickInsights.spendingComparedToLastMonth.contains("less", ignoreCase = true))
                                PrimaryBlue else PrimaryBlue
                        )
                    )
                }


                if (quickInsights.budgetLimitWarning.isNotBlank()) {
                    add(
                        InsightData(
                            icon = Icons.Default.Warning,
                            title = "Budget Alert",
                            description = quickInsights.budgetLimitWarning,
                            color = WarningAmber
                        )
                    )
                }

                if (quickInsights.savingInsights.isNotBlank()) {
                    add(
                        InsightData(
                            icon = Icons.Default.Savings,
                            title = "Savings Update",
                            description = quickInsights.savingInsights,
                            color = SuccessGreen
                        )
                    )
                }
            }

            if (insights.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No insights available at the moment",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(insights) { insight ->
                        InsightCard(
                            icon = insight.icon,
                            title = insight.title,
                            description = insight.description,
                            color = insight.color
                        )
                    }
                }
            }
        } else if (viewmodel.errorMessage.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Unable to load insights. Please try again later.",
                    color = Error,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun InsightCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(100.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 0.dp,
            color = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawColoredShadow(color, 0.1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

private data class InsightData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)