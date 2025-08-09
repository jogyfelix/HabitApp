package com.jolabs.habit.ui.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.jolabs.habit.ui.widgets.habitList.ToggleHabitWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetRefresher {
    fun refreshToggleWidget(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            ToggleHabitWidget().updateAll(context)
        }
    }
}


