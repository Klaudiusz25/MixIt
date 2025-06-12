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
    internal var initialTimerValue by mutableStateOf(0)
    private var job: Job? = null

    private var _cocktail = mutableStateOf<Cocktail?>(null)
    val cocktail: Cocktail? get() = _cocktail.value


    fun loadCocktail(cocktailId: Int) {
        val cocktails = CocktailStorage.loadCocktails(getApplication())
        _cocktail.value = cocktails.find { it.id == cocktailId }
    }

    fun updateNotes(newNotes: String) {
        _cocktail.value?.let { current ->
            val updated = current.copy(notes = newNotes)
            _cocktail.value = updated
            val cocktails = CocktailStorage.loadCocktails(getApplication())
                .map { if (it.id == updated.id) updated else it }
            CocktailStorage.saveCocktails(getApplication(), cocktails)
        }
    }

    fun startTimer() {
        if (timerValue <= 0) timerValue = initialTimerValue // Resetuj, jeśli czas się skończył
        isTimerRunning = true
        job = viewModelScope.launch {
            while (isTimerRunning && timerValue > 0) {
                delay(1000L)
                timerValue-- // Odliczanie w dół
            }
            isTimerRunning = false
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        timerValue = initialTimerValue // Resetuj do początkowego czasu
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

    // Dodaj metodę do ręcznego ustawienia czasu (np. przez użytkownika)
    fun setTimer(newTime: Int) {
        initialTimerValue = newTime
        timerValue = newTime
    }
}