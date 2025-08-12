package com.jolabs.looplog.ui

import UiMessage

sealed class UIEvent {
    data class ShowMessage(val message: UiMessage) : UIEvent()
    data object NavigateUp : UIEvent()
}