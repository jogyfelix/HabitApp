package com.jolabs.looplog.ui

sealed class UIEvent {
    data class ShowMessage(val message: String) : UIEvent()
    data object NavigateUp : UIEvent()
}