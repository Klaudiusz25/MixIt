package com.apkmob.mixit

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    var timerValue by mutableStateOf(0)
    var running by mutableStateOf(false)
    var note by mutableStateOf("")
    var lastRecordedTime by mutableStateOf(0) // Dodane: ostatni zmierzony czas

    private var job: Job? = null

    fun startTimer() {
        running = true
        job = viewModelScope.launch {
            while (running) {
                delay(1000L)
                timerValue++
            }
        }
    }

    fun stopTimer() {
        running = false
        lastRecordedTime = timerValue // Zapisz czas przed resetem
        timerValue = 0
        job?.cancel()
    }

    fun pauseTimer() {
        running = false
        job?.cancel()
    }

    fun resumeTimer() {
        if (!running) {
            running = true
            startTimer() // Wznawia od aktualnej wartości timerValue
        }
    }
}