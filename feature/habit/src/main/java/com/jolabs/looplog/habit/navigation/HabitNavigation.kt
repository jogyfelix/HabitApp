package com.jolabs.looplog.habit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.jolabs.looplog.habit.ui.CreateHabitRoute
import com.jolabs.looplog.habit.ui.HabitHomeRoute
import kotlinx.serialization.Serializable

@Serializable data object HabitHomeRoute
@Serializable data class HabitCreateRoute(val habitId: Long?)

fun NavGraphBuilder.habitNavGraph(onCreatePress : (habitId : Long?) -> Unit,
                                   onNavigateUp : () -> Unit){
     composable<HabitHomeRoute> {
          HabitHomeRoute(onCreatePress = onCreatePress)
     }
     composable<HabitCreateRoute>(
          deepLinks = listOf(
               navDeepLink {
                    uriPattern = "looplog://habit/create?habitId={habitId}"
               }
          ),
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
          ) { backStackEntry ->
          val habitId = backStackEntry.arguments!!.getLong("habitId")
          CreateHabitRoute(onNavigateUp = onNavigateUp,
               habitId = habitId)
     }
}