package com.jolabs.habit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jolabs.habit.ui.CreateHabitRoute
import com.jolabs.habit.ui.CreateHabitScreen
import com.jolabs.habit.ui.HabitHomeScreen
import kotlinx.serialization.Serializable

@Serializable data object HabitHomeRoute
@Serializable data object HabitCreateRoute

fun NavGraphBuilder.habitNavGraph(onCreatePress : () -> Unit,
                                   onNavigateUp : () -> Unit){
     composable<HabitHomeRoute> {
          HabitHomeScreen(onCreatePress = onCreatePress)
     }
     composable<HabitCreateRoute>(
          enterTransition = {
               slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,

               )
          },
          exitTransition = {
               slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
               )
          }) {
          CreateHabitRoute(onNavigateUp = onNavigateUp)
     }
}