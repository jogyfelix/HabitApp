package com.jolabs.ui

sealed class UIEvent {
    data class ShowMessage(val message: String) : UIEvent()
    data object NavigateUp : UIEvent()
}