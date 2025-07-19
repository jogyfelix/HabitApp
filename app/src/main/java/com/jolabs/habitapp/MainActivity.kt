package com.jolabs.habitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jolabs.design_system.ui.theme.HabitAppTheme
import com.jolabs.habit.navigation.HabitCreateRoute
import com.jolabs.habit.navigation.HabitHomeRoute
import com.jolabs.habit.navigation.habitNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitAppTheme {
                val navController = rememberNavController()
              Surface {
                  NavHost(
                      navController = navController,
                      startDestination = HabitHomeRoute
                  ) {
                      habitNavGraph(onCreatePress = {
                          navController.navigate(HabitCreateRoute)
                      },
                          onNavigateUp = {
                              navController.popBackStack()
                          }    )
                  }
              }
            }
        }
    }
}

