package com.apkmob.mixit

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apkmob.mixit.data.Cocktail
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    var timerValue by mutableStateOf(0)
    var isTimerRunning by mutableStateOf(false)
    private var job: Job? = null

    private var _cocktail = mutableStateOf<Cocktail?>(null)
    val cocktail: Cocktail? get() = _cocktail.value

    fun loadCocktail(cocktailId: Int) {
        val cocktails = CocktailStorage.loadCocktails(getApplication())
        _cocktail.value = cocktails.find { it.id == cocktailId }
    }

    fun updateNotes(newNotes: String) {
        _cocktail.value = _cocktail.value?.copy(notes = newNotes)
        _cocktail.value?.let { updated ->
            val cocktails = CocktailStorage.loadCocktails(getApplication())
                .map { if (it.id == updated.id) updated else it }
            CocktailStorage.saveCocktails(getApplication(), cocktails)
        }
    }

    fun startTimer() {
        isTimerRunning = true
        job = viewModelScope.launch {
            while (isTimerRunning) {
                delay(1000L)
                timerValue++
            }
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        timerValue = 0
        job?.cancel()
    }

    fun pauseTimer() {
        isTimerRunning = false
        job?.cancel()
    }

    fun resumeTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            startTimer()
        }
    }
}