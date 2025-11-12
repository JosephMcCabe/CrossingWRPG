package com.example.crossingwrpg

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object WalkBus {
    private val _steps = MutableStateFlow(0L)
    val steps: StateFlow<Long> = _steps

    private val _elapsed = MutableStateFlow(0)
    val elapsed: StateFlow<Int> = _elapsed

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive

    fun publishSteps(v: Long) { _steps.value = v }
    fun publishElapsed(v: Int) { _elapsed.value = v }
    fun publishActive(v: Boolean) { _isActive.value = v }
}