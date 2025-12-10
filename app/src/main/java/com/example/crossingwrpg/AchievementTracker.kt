package com.example.crossingwrpg

import android.content.Context
import android.content.SharedPreferences

class AchievementTracker(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
            "achievements",
            Context.MODE_PRIVATE
        )

    private val achievements = listOf(
        Achievement(
            "A New Beginning",
            "steps_1000",
            1000,
            0,
            0
        ),
        Achievement(
            "Making Progress", "steps_10000",
            10000,
            0,
            0
        ),
        Achievement(
            "True Walker",
            "steps_100000",
            100000,
            0,
            0
        ),
        Achievement(
            "New Slayer",
            "enemies_5",
            0,
            5,
            0
        ),
        Achievement(
            "Dungeon Crawler",
            "enemies_25",
            0,
            25,
            0
        ),
        Achievement(
            "Be Right Back",
            "time_30min",
            0,
            0,
            1800
        )
    )

    fun checkAchievements(steps: Int, enemiesDefeated: Int, walkingSeconds: Int) {
        achievements.forEach { achievement ->
            val meetsRequirement = when {
                achievement.requiredSteps > 0 ->
                    steps >= achievement.requiredSteps
                achievement.requiredEnemies > 0 ->
                    enemiesDefeated >= achievement.requiredEnemies
                achievement.requiredSeconds > 0 ->
                    walkingSeconds >= achievement.requiredSeconds
                else -> false
            }

            if (!isCompleted(achievement.id) && meetsRequirement) {
                markCompleted(achievement.id)
                showNotification(achievement.name)
            }
        }
        checkSecretAchievement()
    }

    fun isCompleted(achievementId: String): Boolean {
        return prefs.getBoolean(
            achievementId,
            false
        )
    }

    fun getCompletedCount(): Int {
        return achievements.count {
            isCompleted(it.id)
        }
    }

    private fun checkSecretAchievement() {
        if (getCompletedCount() >= 6 && !isCompleted("secret_all")) {
            markCompleted("secret_all")
            showNotification("Walking Hero")
        }
    }

    private fun markCompleted(achievementId: String) {
        prefs.edit().putBoolean(achievementId, true).apply()
    }

    private fun showNotification(achievementName: String) {
        Notifications(context).showAchievementNotification(
            title = "🏆 Achievement Complete! 🏆",
            message = achievementName
        )
    }

    private data class Achievement(
        val name: String,
        val id: String,
        val requiredSteps: Int,
        val requiredEnemies: Int,
        val requiredSeconds: Int
    )
}