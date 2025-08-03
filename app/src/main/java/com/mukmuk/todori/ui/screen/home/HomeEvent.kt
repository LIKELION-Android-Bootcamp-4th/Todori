package com.mukmuk.todori.ui.screen.home

sealed class TimerEvent {
    object Start : TimerEvent()
    object Stop : TimerEvent()
    object Reset : TimerEvent()
    object Resume : TimerEvent()
    object Record : TimerEvent()
}