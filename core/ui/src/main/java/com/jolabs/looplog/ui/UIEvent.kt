package com.jolabs.looplog.ui

import UiMessage
import java.time.DayOfWeek

sealed class UIEvent {
    data class ShowMessage(val message: UiMessage) : UIEvent()

    data class SetupAlarm(val habitId: Long,val habitName:String,val timeOfDay: Long, val daysOfWeek: List<DayOfWeek>) : UIEvent()

    data class RemoveAlarm(val habitId:Long,val daysOfWeek: List<DayOfWeek>) : UIEvent()
    data object NavigateUp : UIEvent()
}