package com.nbk.insights.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nbk.insights.ui.theme.InsightsTheme
import com.nbk.insights.ui.theme.*

@Composable
fun EditBudgetDialog(
    budget: BudgetLimit,
    onDismiss: () -> Unit,
    onUpdate: (newLimit: Float) -> Unit,
    onDelete: () -> Unit
) {
    var budgetLimit by remember { mutableStateOf(budget.limit.toString()) }
    var showError by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
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
                            text = "Current spending: KD ${budget.spent.toInt()}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

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
                        onValueChange = {
                            budgetLimit = it.filter { char -> char.isDigit() || char == '.' }
                            showError = false
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
                            focusedBorderColor = NBKBlue,
                            unfocusedBorderColor = Gray200
                        )
                    )

                    if (showError) {
                        Text(
                            text = "Please enter a valid amount",
                            fontSize = 12.sp,
                            color = Error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                if (budgetLimit.isNotEmpty() && budgetLimit.toFloatOrNull() != null) {
                    val newLimit = budgetLimit.toFloat()
                    val difference = newLimit - budget.limit

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                VeryLightGray,
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
                                text = "KD ${budget.limit.toInt()}",
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
                                text = "KD ${newLimit.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (difference > 0) Success else if (difference < 0) Error else Color.Black
                            )
                        }
                    }

                    if (difference != 0f) {
                        Text(
                            text = if (difference > 0) "↗️ Increase of KD ${difference.toInt()}" else "↘️ Decrease of KD ${(-difference).toInt()}",
                            fontSize = 14.sp,
                            color = if (difference > 0) Success else Error,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val amount = budgetLimit.toFloatOrNull()
                            if (amount != null && amount > 0) {
                                onUpdate(amount)
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NBKBlue
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
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Gray500
                            ),
                            border = BorderStroke(1.dp, Gray200)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Error
                            ),
                            border = BorderStroke(1.dp, Error)
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
                        contentColor = Error
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
                "Dining",
                450f,
                400f,
                CategoryDining,
                Icons.Default.Restaurant,
                isOverBudget = true
            ),
            onDismiss = { },
            onUpdate = { },
            onDelete = { }
        )
    }
}