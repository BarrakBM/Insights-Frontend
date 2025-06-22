package com.nbk.insights.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nbk.insights.ui.theme.InsightsTheme
import com.nbk.insights.ui.theme.*

data class BudgetCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun BudgetLimitDialog(
    onDismiss: () -> Unit,
    onConfirm: (category: String, limit: Float) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Dining") }
    var budgetLimit by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val categories = listOf(
        BudgetCategory("Dining", Icons.Default.Restaurant, CategoryDining),
        BudgetCategory("Shopping", Icons.Default.ShoppingBag, CategoryShopping),
        BudgetCategory("Transport", Icons.Default.DirectionsCar, CategoryTransport),
        BudgetCategory("Entertainment", Icons.Default.Movie, CategoryEntertainment),
        BudgetCategory("Utilities", Icons.Default.Bolt, CategoryUtilities),
        BudgetCategory("Healthcare", Icons.Default.LocalHospital, CategoryHealthcare),
        BudgetCategory("Other", Icons.Default.MoreHoriz, CategoryOther)
    )

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
                        text = "Set Budget Limit",
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select Category",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories.size) { index ->
                            val category = categories[index]
                            FilterChip(
                                selected = selectedCategory == category.name,
                                onClick = { selectedCategory = category.name },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            category.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (selectedCategory == category.name)
                                                Color.White else category.color
                                        )
                                        Text(
                                            text = category.name,
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = category.color,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            NBKBlueAlpha10,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = NBKBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "You'll receive notifications when you reach 80% of your budget limit.",
                        fontSize = 12.sp,
                        color = NBKBlue,
                        lineHeight = 16.sp
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

                    Button(
                        onClick = {
                            val amount = budgetLimit.toFloatOrNull()
                            if (amount != null && amount > 0) {
                                onConfirm(selectedCategory, amount)
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NBKBlue
                        )
                    ) {
                        Text(
                            text = "Set Budget",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
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
            onConfirm = { _, _ -> }
        )
    }
}