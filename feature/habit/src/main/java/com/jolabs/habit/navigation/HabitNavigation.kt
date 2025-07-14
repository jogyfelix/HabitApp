package com.jolabs.habit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jolabs.habit.ui.CreateHabitScreen
import com.jolabs.habit.ui.HabitHomeScreen

object HabitHomeDestination {
     val route = "HABIT_HOME"
     val destination = "HABIT_HOME_SCREEN"
}

object HabitCreateDestination {
     val route = "HABIT_CREATE"
     val destination = "HABIT_CREATE_SCREEN"
}

fun NavGraphBuilder.habitNavGraph(navController: NavController){
     composable(route = HabitHomeDestination.route) {
          HabitHomeScreen(onCreatePress = {
               navController.navigate(HabitCreateDestination.route)
          })
     }
     composable(route = HabitCreateDestination.route,
          enterTransition = {
               slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(200)
               )
          },
          exitTransition = {
               slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(500)
               )
          }) {
          CreateHabitScreen()
     }
}