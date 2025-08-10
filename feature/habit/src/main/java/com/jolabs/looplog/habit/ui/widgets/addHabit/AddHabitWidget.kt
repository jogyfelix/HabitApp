package com.jolabs.looplog.habit.ui.widgets.addHabit

import OpenCreateHabitAction
import android.content.Context
import android.os.Build
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import com.jolabs.looplog.design_system.ui.theme.MyWidgetColorScheme
import com.jolabs.looplog.habit.R

class AddHabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
       provideContent {
           GlanceTheme(colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
               GlanceTheme.colors
           } else {
               MyWidgetColorScheme.colors
           }) {
               Box(
                   modifier = GlanceModifier
                       .fillMaxSize()
                       .background(GlanceTheme.colors.background)
                       .clickable(actionRunCallback<OpenCreateHabitAction>()),
                        contentAlignment = Alignment.Center
               ) {
                   Image(
                       provider = ImageProvider(R.drawable.add_task),
                       contentDescription = "Add Habit",
                       colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                       modifier = GlanceModifier.size(40.dp)
                   )
               }
           }
           }
       }
    }