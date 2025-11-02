package com.example.crossingwrpg

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Stopwatch {
    private val timer = MutableStateFlow(0)
    val elapsedTime = timer.asStateFlow()
    private var timerJob: Job? = null

    fun start() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000)
                timer.value += 1
            }
        }

    }

    fun stop() {
        timerJob?.cancel()
    }

    fun reset() {
        timerJob?.cancel()
        timer.value = 0
    }
}