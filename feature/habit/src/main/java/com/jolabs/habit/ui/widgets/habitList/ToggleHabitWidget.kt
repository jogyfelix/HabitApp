package com.jolabs.habit.ui.widgets.habitList

import OpenCreateHabitAction
import android.content.Context
import android.os.Build
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.CheckboxDefaults
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.jolabs.design_system.ui.theme.MyWidgetColorScheme
import com.jolabs.habit.R

class ToggleHabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    GlanceTheme.colors
                } else {
                    MyWidgetColorScheme.colors
                }
            ) {

                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.background)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "Today", style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = GlanceTheme.colors.onBackground
                            )
                        )

                        Spacer(GlanceModifier.defaultWeight())
                        Image(
                            provider = ImageProvider(R.drawable.add),
                            contentDescription = "Add habit",
                            modifier = GlanceModifier
                                .size(16.dp)
                                .clickable(
                                    actionRunCallback<OpenCreateHabitAction>()
                                )
                        )
                    }

                    Spacer(GlanceModifier.height(8.dp))

                    LazyColumn {
                        items(10) {
                            Row {
                                CheckBox(
                                    checked = false,
                                    onCheckedChange = {},
                                    text = "Do something",
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MyWidgetColorScheme.colors.primary,
                                        uncheckedColor = MyWidgetColorScheme.colors.outline,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}