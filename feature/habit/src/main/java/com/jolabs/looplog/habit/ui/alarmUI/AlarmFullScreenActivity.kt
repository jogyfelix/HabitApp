package com.jolabs.looplog.habit.ui.alarmUI

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jolabs.looplog.design_system.ui.theme.HabitAppTheme
import com.jolabs.looplog.habit.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFullScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Dismiss keyguard
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)

        setContent {
            HabitAppTheme {
                Surface() {
                    AlarmScreen(
                        onDismiss = {
                            val notificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.cancel(1001) // cancel by same ID used above
                            finish()
                        },
                        onComplete = {
                            // Define the package name of the module containing MainActivity
                            val packageName = "com.jolabs.looplog"
// Define the fully qualified class name of MainActivity
                            val className = "com.jolabs.looplog.MainActivity"

                            val intent = Intent().apply {
                                component = ComponentName(packageName, className)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            }

// Check if the activity is available before starting
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            } else {
                                // Handle the case where the main module might not be available
                                // or the class name is incorrect.
                            }
                        },
                        habitName = intent.getStringExtra("habitName") ?: "Habit Reminder"
                    )
                }
            }
        }
    }
}


@Composable
fun AlarmScreen(onDismiss: () -> Unit,onComplete:() -> Unit, habitName: String) {
    Scaffold(content = { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.alarm_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(88.dp)
                )
                Text(
                    text = "It's time for $habitName",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    Button(onClick = onComplete) {
                        Text("Mark Habit")
                    }
                }
            }
        }
    })
}

@Preview
@Composable
private fun PreviewAlarmScreen() {
    AlarmScreen(
        {},{}, "Hello"
    )
}