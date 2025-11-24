package com.example.crossingwrpg

class WalkingStateManager {
    var walkState: WalkingState = WalkingState.Idle
    var initialSessionSteps: Long = 0L
    var isPedometerActive: Boolean = false
    var appTotalSteps: Long = 0L
    var stepsBeforePause: Long = 0L
    var isResuming: Boolean = false
    var earnedItemsList: Map<Long, Int> = emptyMap()
}