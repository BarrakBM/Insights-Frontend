package com.nbk.insights.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nbk.insights.ui.theme.InsightsTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditBudgetDialog(
    budget: BudgetLimit,
    onDismiss: () -> Unit,
    onUpdate: (newLimit: BigDecimal, renewsAt: String) -> Unit,
    onDelete: () -> Unit
) {
    var budgetLimit by remember { mutableStateOf(budget.limit.setScale(3, RoundingMode.HALF_UP).toPlainString()) }
    var selectedDay by remember {
        mutableStateOf(
            try {
                LocalDate.parse(budget.renewsAt).dayOfMonth
            } catch (e: Exception) {
                1 // Default to 1st of month if parsing fails
            }
        )
    }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val currentDay = today.dayOfMonth

    // Calculate the renewal date based on selected day
    val renewalDate = remember(selectedDay) {
        val targetDate = if (selectedDay <= currentDay) {
            // If selected day is today or in the past, use current month
            today.withDayOfMonth(selectedDay)
        } else {
            // If selected day is in the future, use previous month
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
            else BigDecimal(input).setScale(3, RoundingMode.HALF_UP) // KWD has 3 decimal places
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Helper function to validate decimal input
    fun isValidDecimalInput(input: String): Boolean {
        if (input.isEmpty()) return true

        // Allow digits, one decimal point, and up to 3 decimal places
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
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Budget Limit",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                // Category Display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            budget.color.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(budget.color.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            budget.icon,
                            contentDescription = null,
                            tint = budget.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = budget.category,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Current spending: KD ${budget.spent.setScale(3, RoundingMode.HALF_UP).toPlainString()}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Budget Limit Input
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Monthly Budget Limit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = budgetLimit,
                        onValueChange = { newValue ->
                            if (isValidDecimalInput(newValue)) {
                                budgetLimit = newValue
                                showError = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter amount in KD",
                                color = Color.Gray
                            )
                        },
                        prefix = {
                            Text(
                                text = "KD ",
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        isError = showError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E3A8A),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                }

                // Renewal Day Selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Budget Renewal Day",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Text(
                        text = "Select the day of the month when your budget resets",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    // Day selector with a more compact grid
                    val dayChunks = (1..31).chunked(7)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dayChunks.forEach { dayRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                dayRow.forEach { day ->
                                    FilterChip(
                                        selected = selectedDay == day,
                                        onClick = { selectedDay = day },
                                        label = {
                                            Text(
                                                text = day.toString(),
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.width(20.dp)
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF1E3A8A),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                                // Fill remaining space if row has fewer than 7 items
                                repeat(7 - dayRow.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Show calculated renewal date
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E3A8A).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF1E3A8A),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Budget will renew on: ${LocalDate.parse(renewalDate).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                                fontSize = 12.sp,
                                color = Color(0xFF1E3A8A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Current vs New Comparison
                parseBudgetAmount(budgetLimit)?.let { newLimit ->
                    val difference = newLimit.subtract(budget.limit)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF8FAFC),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current Limit",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "KD ${budget.limit.setScale(3, RoundingMode.HALF_UP).toPlainString()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Column {
                            Text(
                                text = "New Limit",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "KD ${newLimit.setScale(3, RoundingMode.HALF_UP).toPlainString()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    difference > BigDecimal.ZERO -> Color(0xFF10B981)
                                    difference < BigDecimal.ZERO -> Color(0xFFEF4444)
                                    else -> Color.Black
                                }
                            )
                        }
                    }

                    if (difference != BigDecimal.ZERO) {
                        Text(
                            text = if (difference > BigDecimal.ZERO) {
                                "↗️ Increase of KD ${difference.setScale(3, RoundingMode.HALF_UP).toPlainString()}"
                            } else {
                                "↘️ Decrease of KD ${difference.abs().setScale(3, RoundingMode.HALF_UP).toPlainString()}"
                            },
                            fontSize = 14.sp,
                            color = if (difference > BigDecimal.ZERO) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // Error message
                if (showError) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Info Message
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF10B981).copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "You'll receive notifications when you reach 80% of your budget limit.",
                        fontSize = 12.sp,
                        color = Color(0xFF10B981),
                        lineHeight = 16.sp
                    )
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Update Button
                    Button(
                        onClick = {
                            val amount = parseBudgetAmount(budgetLimit)
                            if (amount != null && amount > BigDecimal.ZERO) {
                                onUpdate(amount, renewalDate)
                            } else {
                                showError = true
                                errorMessage = if (budgetLimit.isBlank()) {
                                    "Please enter a budget amount"
                                } else {
                                    "Please enter a valid amount greater than 0"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A8A)
                        )
                    ) {
                        Text(
                            text = "Update Budget",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Delete Button
                        OutlinedButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Delete",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Delete Budget Limit",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete the budget limit for ${budget.category}? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Preview
@Composable
fun EditBudgetDialogPreview() {
    InsightsTheme {
        EditBudgetDialog(
            budget = BudgetLimit(
                category = "Dining",
                spent = BigDecimal("450.000"),
                limit = BigDecimal("400.000"),
                color = Color(0xFFEF4444),
                icon = Icons.Default.Restaurant,
                isOverBudget = true,
                renewsAt = "2025-07-15"
            ),
            onDismiss = { },
            onUpdate = { _, _ -> },
            onDelete = { }
        )
    }
}