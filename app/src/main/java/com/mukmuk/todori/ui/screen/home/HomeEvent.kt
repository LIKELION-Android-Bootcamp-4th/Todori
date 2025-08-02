package com.mukmuk.todori.ui.screen.home

sealed class TimerEvent {
    object Start : TimerEvent()
    object Resume : TimerEvent()
    object Stop : TimerEvent()
    object Reset : TimerEvent()
}
