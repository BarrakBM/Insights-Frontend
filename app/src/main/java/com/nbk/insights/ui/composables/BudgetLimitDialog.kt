package com.nbk.insights.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nbk.insights.data.dtos.Category
import com.nbk.insights.ui.theme.InsightsTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

data class BudgetCategoryUI(
    val category: Category,
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val description: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetLimitDialog(
    onDismiss: () -> Unit,
    onConfirm: (category: Category, limit: BigDecimal, renewsAt: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(Category.DINING) }
    var budgetLimit by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(1) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = listOf(
        BudgetCategoryUI(
            category = Category.DINING,
            displayName = "Dining",
            icon = Icons.Default.Restaurant,
            color = Color(0xFFEF4444),
            description = "Restaurants & cafes"
        ),
        BudgetCategoryUI(
            category = Category.SHOPPING,
            displayName = "Shopping",
            icon = Icons.Default.ShoppingBag,
            color = Color(0xFF3B82F6),
            description = "Retail & online"
        ),
        BudgetCategoryUI(
            category = Category.ENTERTAINMENT,
            displayName = "Entertainment",
            icon = Icons.Default.Movie,
            color = Color(0xFF8B5CF6),
            description = "Movies & activities"
        ),
        BudgetCategoryUI(
            category = Category.FOOD_AND_GROCERIES,
            displayName = "Groceries",
            icon = Icons.Default.ShoppingCart,
            color = Color(0xFFF59E0B),
            description = "Food & essentials"
        ),
        BudgetCategoryUI(
            category = Category.OTHER,
            displayName = "Other",
            icon = Icons.Default.MoreHoriz,
            color = Color(0xFF6B7280),
            description = "Everything else"
        )
    )

    val today = LocalDate.now()
    val currentDay = today.dayOfMonth

    // Calculate the renewal date based on selected day
    val renewalDate = remember(selectedDay) {
        val targetDate = if (selectedDay <= currentDay) {
            today.withDayOfMonth(selectedDay)
        } else {
            today.minusMonths(1).withDayOfMonth(
                minOf(selectedDay, today.minusMonths(1).lengthOfMonth())
            )
        }
        targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    // Helper function to parse BigDecimal from string
    fun parseBudgetAmount(input: String): BigDecimal? {
        return try {
            if (input.isBlank()) null
            else BigDecimal(input).setScale(3, RoundingMode.HALF_UP)
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Helper function to validate decimal input
    fun isValidDecimalInput(input: String): Boolean {
        if (input.isEmpty()) return true
        val regex = Regex("^\\d*\\.?\\d{0,3}$")
        return regex.matches(input)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E3A8A).copy(alpha = 0.08f),
                                    Color(0xFF1E3A8A).copy(alpha = 0.02f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Set Budget Limit",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Control your monthly spending",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Category Selection with enhanced visuals
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Select Category",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            )
                            AnimatedVisibility(visible = selectedCategory != null) {
                                val selectedCategoryUI =
                                    categories.find { it.category == selectedCategory }
                                Text(
                                    text = selectedCategoryUI?.description ?: "",
                                    fontSize = 12.sp,
                                    color = selectedCategoryUI?.color ?: Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // tighter spacing
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { categoryUI ->
                                val isSelected = selectedCategory == categoryUI.category
                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.05f else 1f,
                                    label = "scale"
                                )

                                ElevatedCard(
                                    modifier = Modifier
                                        .width(80.dp) // narrower card
                                        .scale(scale)
                                        .clickable { selectedCategory = categoryUI.category },
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = if (isSelected)
                                            categoryUI.color
                                        else
                                            Color(0xFFF8FAFC)
                                    ),
                                    elevation = CardDefaults.elevatedCardElevation(
                                        defaultElevation = if (isSelected) 4.dp else 1.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp), // tighter padding
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp) // smaller gap
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp) // smaller background circle
                                                .background(
                                                    if (isSelected)
                                                        Color.White.copy(alpha = 0.2f)
                                                    else
                                                        categoryUI.color.copy(alpha = 0.1f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                categoryUI.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp), // smaller icon
                                                tint = if (isSelected)
                                                    Color.White
                                                else
                                                    categoryUI.color
                                            )
                                        }
                                        Text(
                                            text = categoryUI.displayName,
                                            fontSize = 9.sp, // smaller text
                                            fontWeight = if (isSelected)
                                                FontWeight.SemiBold
                                            else
                                                FontWeight.Normal,
                                            color = if (isSelected)
                                                Color.White
                                            else
                                                Color(0xFF374151),

                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Amount input with better validation
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Monthly Budget Limit",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            )
                            AnimatedVisibility(visible = budgetLimit.isNotEmpty()) {
                                Text(
                                    text = "Max 3 decimal places",
                                    fontSize = 12.sp,
                                    color = if (amountError) Color(0xFFEF4444) else Color(0xFF9CA3AF)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = budgetLimit,
                            onValueChange = { newValue ->
                                if (isValidDecimalInput(newValue)) {
                                    budgetLimit = newValue
                                    showError = false
                                    amountError = false
                                } else {
                                    amountError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "0.000",
                                    color = Color(0xFF9CA3AF)
                                )
                            },
                            label = { Text("Enter amount") },
                            prefix = {
                                Text(
                                    text = "KD ",
                                    color = Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            isError = showError || amountError,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E3A8A),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                errorBorderColor = Color(0xFFEF4444)
                            ),
                            supportingText = {
                                if (amountError) {
                                    Text(
                                        "Invalid format. Use numbers and up to 3 decimal places.",
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                    }

                    // Renewal Day Selection with enhanced visuals
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Budget Renewal Day",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = "When your budget resets each month",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            if (selectedDay == currentDay) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF1E3A8A).copy(alpha = 0.1f)
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        Color(0xFF1E3A8A).copy(alpha = 0.3f)
                                    )
                                ) {
                                    Text(
                                        text = "Today",
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        ),
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E3A8A),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = when (selectedDay) {
                                    1 -> "1st of every month"
                                    2 -> "2nd of every month"
                                    3 -> "3rd of every month"
                                    21 -> "21st of every month"
                                    22 -> "22nd of every month"
                                    23 -> "23rd of every month"
                                    31 -> "31st of every month"
                                    else -> "${selectedDay}th of every month"
                                },
                                onValueChange = { },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                label = { Text("Select day") },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .padding(8.dp)
                                            .background(
                                                Color(0xFF1E3A8A).copy(alpha = 0.1f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = selectedDay.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                    }
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1E3A8A),
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                    focusedLabelColor = Color(0xFF1E3A8A)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.heightIn(max = 300.dp)
                            ) {
                                (1..31).forEach { day ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    when (day) {
                                                        1 -> "1st"
                                                        2 -> "2nd"
                                                        3 -> "3rd"
                                                        21 -> "21st"
                                                        22 -> "22nd"
                                                        23 -> "23rd"
                                                        31 -> "31st"
                                                        else -> "${day}th"
                                                    },
                                                    fontWeight = if (selectedDay == day)
                                                        FontWeight.SemiBold
                                                    else
                                                        FontWeight.Normal
                                                )
                                                if (day == currentDay) {
                                                    Text(
                                                        "Today",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF1E3A8A),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedDay = day
                                            expanded = false
                                        },
                                        leadingIcon = {
                                            if (selectedDay == day) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color(0xFF1E3A8A),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = if (selectedDay == day)
                                                Color(0xFF1E3A8A)
                                            else
                                                Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Error message with icon
                    AnimatedVisibility(visible = showError) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFEF4444).copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = errorMessage,
                                    fontSize = 13.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    // Action buttons with better spacing and states
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF374151)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                val amount = parseBudgetAmount(budgetLimit)
                                if (amount != null && amount > BigDecimal.ZERO) {
                                    onConfirm(selectedCategory, amount, renewalDate)
                                } else {
                                    showError = true
                                    errorMessage = if (budgetLimit.isBlank()) {
                                        "Please enter a budget amount"
                                    } else {
                                        "Please enter a valid amount greater than 0"
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            ),
                            enabled = budgetLimit.isNotEmpty() && !amountError,
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set Budget",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BudgetLimitDialogPreview() {
    InsightsTheme {
        BudgetLimitDialog(
            onDismiss = { },
            onConfirm = { _, _, _ -> }
        )
    }
}