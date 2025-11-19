package com.example.crossingwrpg

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


enum class WalkingState { Idle, Walking, Paused }
class WalkingViewModel {
    data class WalkUiState(
        var walkState: WalkingState = WalkingState.Idle,
        val sessionSteps: Long = 0L,
        val elapsedSeconds: Int = 0,
        val earnedItems: List<EarnedItem> = emptyList()
    )

    data class WalkResult(
        val steps: Int,
        val time: Int,
        val earnedItems: List<EarnedItem>
    )

    class WalkingViewModel(
        private val notifications: Notifications,
        appContext: Context
    ) : ViewModel() {
        private val _ui = MutableStateFlow(WalkUiState())
        val ui: StateFlow<WalkUiState> = _ui.asStateFlow()

        init {
            WalkManager.ensureInit(appContext)
            viewModelScope.launch {
                WalkManager.totalSteps.collectLatest { step ->
                    _ui.value = _ui.value.copy(sessionSteps = step)
                }
            }
            viewModelScope.launch {
                WalkManager.elapsedSeconds.collectLatest { sec ->
                    _ui.value = _ui.value.copy(elapsedSeconds = sec)

                }
            }
        }

        fun onStartClicked() {
            WalkManager.startSession()
            _ui.value = _ui.value.copy(
                walkState = WalkingState.Walking,
                sessionSteps = 0L,
                elapsedSeconds = 0,
                earnedItems = emptyList()
            )
        }

        fun onPauseClicked() {
            WalkManager.pauseSession()
            _ui.value = _ui.value.copy(
                walkState = WalkingState.Paused,
            )
        }
        fun onResumeClicked() {
            WalkManager.resumeSession()
            _ui.value = _ui.value.copy(
                walkState = WalkingState.Walking,
            )
        }

        fun onStopClicked(): WalkResult{
            WalkManager.stopSession()
            val result = WalkResult(
                steps = _ui.value.sessionSteps.toInt(),
                time = _ui.value.elapsedSeconds,
                earnedItems = _ui.value.earnedItems
            )
            _ui.value = WalkUiState()
            return result
        }

        private fun grantItems(sessionSteps: Long) {
            if (sessionSteps < 10) return

            val current = _ui.value.earnedItems
            val currentById = current.associateBy { it.id }
            val increments = ALL_DROPPABLE_ITEMS.mapNotNull { tpl ->
                val maxPossible = (sessionSteps / tpl.dropThreshold).toInt()
                val already = currentById[tpl.id]?.count ?: 0
                val add = maxPossible - already
                if (add > 0) tpl to add else null
            }
            if (increments.isEmpty()) return

            val updatesById = increments.associate { (tpl, add) ->
                val old = currentById[tpl.id]?.count ?: 0
                tpl.copy(count = old + add) to tpl.id
            }.entries.associate { (item, id) -> id to item }

            val merged = buildList {
                current.forEach { existing ->
                    add(updatesById[existing.id] ?: existing)
                }
                ALL_DROPPABLE_ITEMS.forEach { tpl ->
                    if (currentById[tpl.id] == null && updatesById[tpl.id] != null) {
                        add(updatesById[tpl.id]!!)
                    }
                }
            }

            _ui.update { it.copy(earnedItems = merged) }

            val msg = increments.joinToString(", ") { (tpl, add) -> "$add ${tpl.name}" }
            notifications.showItemNotification(
                title = "As you were walking.",
                message = "You found: $msg"
            )
        }
    }
}