package com.nbk.insights.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 *  ►  Purpose
 *     • keeps a central cache of raw-resource Lottie compositions
 *     • exposes a keyed map so Composables never repeat the same parsing work
 *     • optional global flags (loading, success, error…) so any screen can show / hide animations
 */
class AnimationViewModel(app: Application) : AndroidViewModel(app) {

    /** in-memory cache so the same RawRes isn’t parsed twice */
    private val compositionCache =
        mutableMapOf<Int, LottieCompositionSpec>()

    /** UI state flags you can toggle from anywhere */
    private val _isGlobalLoading = MutableStateFlow(false)
    val isGlobalLoading: StateFlow<Boolean> = _isGlobalLoading

    fun setGlobalLoading(show: Boolean) {
        _isGlobalLoading.value = show
    }

    /** Get (and memo-store) a LottieCompositionSpec for a raw file */
    fun spec(rawRes: Int): LottieCompositionSpec =
        compositionCache.getOrPut(rawRes) {
            LottieCompositionSpec.RawRes(rawRes)
        }
}