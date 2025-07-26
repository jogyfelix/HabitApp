package com.jolabs.habit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jolabs.habit.ui.CreateHabitRoute
import com.jolabs.habit.ui.HabitHomeRoute
import com.jolabs.habit.ui.HabitHomeScreen
import kotlinx.serialization.Serializable

@Serializable data object HabitHomeRoute
@Serializable data object HabitCreateRoute

fun NavGraphBuilder.habitNavGraph(onCreatePress : () -> Unit,
                                   onNavigateUp : () -> Unit){
     composable<HabitHomeRoute> {
          HabitHomeRoute(onCreatePress = onCreatePress)
     }
     composable<HabitCreateRoute>(
          enterTransition = {
               slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
               )
          },
          exitTransition = {
               slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
               )
          },
          popEnterTransition = {
               slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                    )
          },
          popExitTransition = {
               slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
               )
          }
          ) {
          CreateHabitRoute(onNavigateUp = onNavigateUp)
     }
}