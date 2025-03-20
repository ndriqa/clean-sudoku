package com.ndriqa.cleansudoku.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry

typealias TransitionScope = @JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>

object NavigationTransitions {
    private const val Duration = 250

    val slideInAnim: (TransitionScope.() -> EnterTransition) = { slideIn(
        animationSpec = tween(Duration),
        initialOffset = { IntOffset(it.width, 0) } ,
    ) }

    val slideOutAnim: (TransitionScope.() -> ExitTransition) = { slideOut(
        animationSpec = tween(Duration),
        targetOffset = { IntOffset(-it.width, 0) }
    ) }
}