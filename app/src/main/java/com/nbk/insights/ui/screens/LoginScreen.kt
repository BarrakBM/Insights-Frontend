package com.nbk.insights.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.R
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.composables.LoadingIndicator
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AuthViewModel

@Composable
fun LoginScreen(navController: NavController) {
    /* ── VM & state ───────────────────────────────────────── */
    val ctx = LocalContext.current
    val viewModel: AuthViewModel =
        viewModel(factory = remember { AppInitializer.provideAuthViewModelFactory(ctx) })

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading    by viewModel.isLoading
    val user         by viewModel.user
    val token        by viewModel.token
    val errorMessage by viewModel.errorMessage

    /* Navigate once authenticated */
    LaunchedEffect(token?.token, user, isLoading) {
        if (!token?.token.isNullOrBlank() && user != null && !isLoading) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    if (isLoading && user == null) {
        LoadingIndicator()
        return
    }

    /* ── Glass-morphism UI ────────────────────────────────── */
    Box(
        modifier = Modifier
            .fillMaxSize()
            /* Gradient wallpaper */
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF4472C4), // Primary blue from app
                        Color(0xFF4472C4).copy(alpha = 0.8f),
                        Color(0xFFF5F5F5) // Light background
                    )
                )
            )
    ) {
        /* Frosted card that holds the form */
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(32.dp)
                .widthIn(max = 420.dp)
        ) {

            /* ───── Logo ───── */
            Image(
                painter = painterResource(R.drawable.insights_plus_logo),
                contentDescription = "Insights+ logo",
                colorFilter = ColorFilter.tint(
                    Color(0xFF4472C4), // Primary blue for logo
                    BlendMode.SrcIn
                ),
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(32.dp))

            /* ───── Email ───── */
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                placeholder   = { Text("Username", color = Color(0xFF666666)) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Color(0xFF4472C4),
                    unfocusedBorderColor    = Color(0xFFE0E0E0),
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor             = Color(0xFF4472C4),
                    focusedTextColor        = Color(0xFF333333),
                    unfocusedTextColor      = Color(0xFF333333)
                )
            )

            Spacer(Modifier.height(16.dp))

            /* ───── Password ───── */
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                placeholder   = { Text("Password", color = Color(0xFF666666)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Color(0xFF4472C4),
                    unfocusedBorderColor    = Color(0xFFE0E0E0),
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor             = Color(0xFF4472C4),
                    focusedTextColor        = Color(0xFF333333),
                    unfocusedTextColor      = Color(0xFF333333)
                )
            )

            Spacer(Modifier.height(28.dp))

            /* ───── Primary – Login ───── */
            Button(
                onClick  = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF4472C4)),
                shape   = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Login",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ───── Secondary – Dummy login (outlined glass) ───── */
            OutlinedButton(
                onClick  = { viewModel.dummyLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4472C4)
                ),
                border = BorderStroke(1.dp, Color(0xFF4472C4))
            ) {
                Text(
                    "Dummy Login (Test)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            /* ───── Error ───── */
            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    errorMessage!!,
                    color = Color(0xFFE53E3E),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ───── Sign-up link ───── */
            Text(
                text     = "Don't have an account? Sign up",
                color    = Color(0xFF4472C4),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { navController.navigate(Screen.Register.route) }
            )
        }
    }
}