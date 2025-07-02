package com.nbk.insights.ui.composables

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nbk.insights.R
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
            // Show loading state
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
            // Create insights list from the DTO
            val insights = buildList {
                // Saving insights with camel icon - FIRST
                if (quickInsights.savingInsights.isNotBlank()) {
                    add(
                        InsightData(
                            customIcon = { tintColor ->
                                CamelIcon(
                                    modifier = Modifier.size(20.dp),
                                    tint = tintColor
                                )
                            },
                            title = "Savings Update",
                            description = quickInsights.savingInsights,
                            color = if (quickInsights.spendingComparedToLastMonth.contains("less", ignoreCase = true))
                                PrimaryBlue else Error
                        )
                    )
                }

                // Spending comparison insight - MIDDLE
                if (quickInsights.spendingComparedToLastMonth.isNotBlank()) {
                    add(
                        InsightData(
                            icon = if (quickInsights.spendingComparedToLastMonth.contains("less", ignoreCase = true))
                                Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                            title = "Monthly Spending Update",
                            description = quickInsights.spendingComparedToLastMonth,
                            color = SuccessGreen
                        )
                    )
                }

                // Budget limit warning - LAST
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
            }

            if (insights.isEmpty()) {
                // Show empty state
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
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(insights) { insight ->
                        InsightCard(
                            icon = insight.icon,
                            customIcon = insight.customIcon,
                            title = insight.title,
                            description = insight.description,
                            color = insight.color
                        )
                    }
                }
            }
        } else if (viewmodel.errorMessage.value != null) {
            // Show error state
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun InsightCard(
    icon: ImageVector? = null,
    customIcon: @Composable ((Color) -> Unit)? = null,
    title: String,
    description: String,
    color: Color
) {
    var isExpanded by remember { mutableStateOf(false) }
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 160.dp else 105.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "height"
    )
    val elevation by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 2.dp,
        animationSpec = spring(),
        label = "elevation"
    )

    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .width(280.dp)
            .height(animatedHeight)
            .shadow(elevation, RoundedCornerShape(12.dp))
            .zIndex(if (isExpanded) 1f else 0f),
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(2.dp))
            )

            // Icon container that handles both regular and custom icons
            Box(
                modifier = Modifier.size(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (customIcon != null) {
                    customIcon(color)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedContent(
                    targetState = isExpanded,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150, 150)) with
                                fadeOut(animationSpec = tween(150)) using
                                SizeTransform { initialSize, targetSize ->
                                    if (targetState) {
                                        keyframes {
                                            IntSize(targetSize.width, initialSize.height) at 150
                                            durationMillis = 300
                                        }
                                    } else {
                                        keyframes {
                                            IntSize(initialSize.width, targetSize.height) at 150
                                            durationMillis = 300
                                        }
                                    }
                                }
                    },
                    label = "text"
                ) { expanded ->
                    Text(
                        description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
                    )
                }

                // Show expand/collapse indicator
                if (!isExpanded && description.length > 70) {
                    Text(
                        "Tap to read more",
                        fontSize = 10.sp,
                        color = color,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CamelIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    Icon(
        painter = painterResource(id = R.drawable.nbk_kw),
        contentDescription = "Camel",
        modifier = modifier,
        tint = tint
    )
}

// Data class to hold insight information
private data class InsightData(
    val icon: ImageVector? = null,
    val customIcon: @Composable ((Color) -> Unit)? = null,
    val title: String,
    val description: String,
    val color: Color
)