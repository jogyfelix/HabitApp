package com

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jolabs.looplog.habit.workers.UpdateHabitsWorker
import com.jolabs.looplog.habit.utils.WidgetUpdateUtil
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HabitApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleDailyWidgetRefresh()
        WidgetUpdateUtil.refreshHabitWidgets(applicationContext)
    }

    private fun scheduleDailyWidgetRefresh() {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atTime(LocalTime.MIDNIGHT).atZone(zone)
        val initialDelayMinutes = Duration.between(now,nextMidnight).toMinutes()

        val request = PeriodicWorkRequestBuilder<UpdateHabitsWorker>(1, TimeUnit.DAYS).setInitialDelay(initialDelayMinutes,
            TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_widget_refresh",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

    }
}
