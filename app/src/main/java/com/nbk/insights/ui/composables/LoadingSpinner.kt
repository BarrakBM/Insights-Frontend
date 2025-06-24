package com.nbk.insights.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nbk.insights.R
import com.nbk.insights.viewmodels.AnimationViewModel

@Composable
fun LoadingSpinner(
    animationVM: AnimationViewModel = viewModel(),
    iterations: Int = LottieConstants.IterateForever,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        animationVM.spec(R.raw.loading)   // just ask the VM
    )
    val progress by animateLottieCompositionAsState(
        composition, iterations = iterations
    )

    LottieAnimation(
        composition = composition,
        progress    = { progress },
        modifier    = modifier.size(96.dp)
    )
}