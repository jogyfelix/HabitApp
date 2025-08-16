package com.jolabs.looplog

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.jolabs.looplog.design_system.ui.theme.HabitAppTheme
import com.jolabs.looplog.habit.workers.RescheduleAlarmWorker
import com.jolabs.looplog.habit.navigation.HabitCreateRoute
import com.jolabs.looplog.habit.navigation.HabitHomeRoute
import com.jolabs.looplog.habit.navigation.habitNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val showUpdateDialog = mutableStateOf(false)

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            // you may choose to retry / log / fallback to redirect to Play Store
        }
    }

    private val installListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdateDialog.value = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installListener)
        checkForUpdates()
        createNotificationChannel()

        setContent {
            HabitAppTheme {
                val navController = rememberNavController()
              Surface() {
                  NavHost(
                      navController = navController,
                      startDestination = HabitHomeRoute
                  ) {
                      habitNavGraph(onCreatePress = {
                          navController.navigate(HabitCreateRoute(habitId = it)) {launchSingleTop = true}
                      },
                          onNavigateUp = {
                              navController.popBackStack()
                          }    )
                  }


                  if (showUpdateDialog.value) {
                      AlertDialog(
                          onDismissRequest = {
                              showUpdateDialog.value = false
                          },
                          title = { Text(stringResource(R.string.update_ready)) },
                          text = { Text(stringResource(R.string.new_version_desc)) },
                          confirmButton = {
                              TextButton(onClick = {
                                  showUpdateDialog.value = false
                                  appUpdateManager.completeUpdate()
                              }) {
                                  Text(stringResource(R.string.install))
                              }
                          },
                          dismissButton = {
                              TextButton(onClick = {
                                  showUpdateDialog.value = false
                              }) {
                                  Text(stringResource(R.string.later))
                              }
                          }
                      )
                  }
              }
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "looplog_alarm_channel",
            "Looplog Alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for looplog habit reminders"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Test method to verify WorkManager functionality
    private fun testWorkManager() {
        try {
            android.util.Log.d("MainActivity", "Testing WorkManager...")
            val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmWorker>().build()
            WorkManager.getInstance(this).enqueueUniqueWork(
                "test_reschedule_alarms",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            android.util.Log.d("MainActivity", "WorkManager test successful - work enqueued")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "WorkManager test failed", e)
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when (info.updateAvailability()) {
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    // Resume immediate update flow
                    if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        )
                    }
                }
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    // Optional: resume flexible flow if allowed
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                        )
                    }
                }
                else -> {
                    // If update already downloaded, complete it
                    if (info.installStatus() == InstallStatus.DOWNLOADED) {
                        showUpdateDialog.value = true
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        appUpdateManager.unregisterListener(installListener)
        super.onDestroy()
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isImmediateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            val isFlexibleAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            val priority = appUpdateInfo.updatePriority() // 0..5 set via Play API
            val staleness = appUpdateInfo.clientVersionStalenessDays() ?: -1

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                when {
                    // choose your own thresholds:
                    isImmediateAllowed && (priority >= 4 || staleness >= 7) -> {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        )
                    }
                    isFlexibleAllowed -> {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                        )
                    }
                }
            }
        }
    }
}

