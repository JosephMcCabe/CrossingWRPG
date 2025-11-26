package com.example.crossingwrpg

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

object WalkManager {
    @Volatile private var initialized = false
    private lateinit var pedometer: Pedometer
    private val stopwatch = Stopwatch()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // baseline for accurate steps
    val baseline = MutableStateFlow<Long?>(null)

    // total steps
    val totalSteps: StateFlow<Long> by lazy {
        pedometer.stepCount.stateIn(scope, SharingStarted.Eagerly, 0L)
    }

    val sessionSteps: StateFlow<Long> by lazy {
        totalSteps
            .map { steps ->
                val base = baseline.value ?: steps
                (steps - base).coerceAtLeast(0L)
            }
            .stateIn(scope, SharingStarted.Eagerly, 0L)
    }

    // elapsed seconds
    val elapsedSeconds: StateFlow<Int> by lazy {
        stopwatch.elapsedTime.stateIn(scope, SharingStarted.Eagerly, 0)
    }

    // ensureInit
    fun ensureInit(appContext: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            pedometer = Pedometer(appContext.applicationContext)
            initialized = true
        }
    }

    // startSession
    fun startSession() {
        baseline.value = totalSteps.value
        stopwatch.reset()
        pedometer.start()
        stopwatch.start()
        pedometer.debugAddSteps(100)
    }

    // pauseSession
    fun pauseSession() {
        pedometer.stop()
        stopwatch.stop()
    }

    // resumeSession
    fun resumeSession() {
        pedometer.start()
        stopwatch.start()
    }

    // stopSession
    fun stopSession() {
        pedometer.stop()
        stopwatch.stop()
    }
}