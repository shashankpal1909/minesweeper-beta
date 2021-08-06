package com.example.minesweeper_beta

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val mainMenuButton = findViewById<Button>(R.id.mainMenuStatsButton)
        mainMenuButton.setOnClickListener {
            finish()
        }

        val lastGameStatsTextView = findViewById<TextView>(R.id.lastGameStatsTextView)
        val sharedPrefLastGame = getSharedPreferences("LAST_GAME_STATS", Context.MODE_PRIVATE)

        setLastGameStats(lastGameStatsTextView, sharedPrefLastGame)
        setGameStats()

        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            listOf(
                "STANDARD",
                "BEGINNER",
                "INTERMEDIATE",
                "EXPERT",
                "MASTER",
                "CUSTOM",
                "LAST_GAME_STATS"
            ).forEach {
                getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().apply()
            }
            setLastGameStats(lastGameStatsTextView, sharedPrefLastGame)
            setGameStats()
        }


    }

    private fun setLastGameStats(
        lastGameStatsTextView: TextView,
        sharedPrefLastGame: SharedPreferences
    ) {
        lastGameStatsTextView.text = sharedPrefLastGame.getString("STATS", "No Games Played!")
    }

    private fun setGameStats() {
        setStandardModeStats()
        setBeginnerModeStats()
        setIntermediateModeStats()
        setExpertModeStats()
        setMasterModeStats()
        setCustomModeStats()
    }

    private fun setCustomModeStats() {
        val sharedPrefCustom = getSharedPreferences("CUSTOM", MODE_PRIVATE)
        findViewById<TextView>(R.id.totalGamesCustom).text =
            sharedPrefCustom.getInt("TOTAL_GAMES_COUNT", 0).toString()
        findViewById<TextView>(R.id.totalTimeCustom).text =
            getTimeString(sharedPrefCustom.getInt("TOTAL_TIME", 0))
        findViewById<TextView>(R.id.shortestTimeCustom).text =
            getShortestTimeString(sharedPrefCustom.getInt("SHORTEST_TIME", -1))
        findViewById<TextView>(R.id.totalGamesLostCustom).text =
            sharedPrefCustom.getInt("TOTAL_GAMES_LOST", 0).toString()
        findViewById<TextView>(R.id.totalGameWonCustom).text =
            sharedPrefCustom.getInt("TOTAL_GAMES_WON", 0).toString()
    }

    private fun setMasterModeStats() {
        val sharedPrefMaster = getSharedPreferences("MASTER", MODE_PRIVATE)
        findViewById<TextView>(R.id.totalGamesMaster).text =
            sharedPrefMaster.getInt("TOTAL_GAMES_COUNT", 0).toString()
        findViewById<TextView>(R.id.totalTimeMaster).text =
            getTimeString(sharedPrefMaster.getInt("TOTAL_TIME", 0))
        findViewById<TextView>(R.id.shortestTimeMaster).text =
            getShortestTimeString(sharedPrefMaster.getInt("SHORTEST_TIME", -1))
        findViewById<TextView>(R.id.totalGamesLostMaster).text =
            sharedPrefMaster.getInt("TOTAL_GAMES_LOST", 0).toString()
        findViewById<TextView>(R.id.totalGameWonMaster).text =
            sharedPrefMaster.getInt("TOTAL_GAMES_WON", 0).toString()
    }

    private fun setExpertModeStats() {
        val sharedPrefExpert = getSharedPreferences("EXPERT", MODE_PRIVATE)
        findViewById<TextView>(R.id.totalGamesExpert).text =
            sharedPrefExpert.getInt("TOTAL_GAMES_COUNT", 0).toString()
        findViewById<TextView>(R.id.totalTimeExpert).text =
            getTimeString(sharedPrefExpert.getInt("TOTAL_TIME", 0))
        findViewById<TextView>(R.id.shortestTimeExpert).text =
            getShortestTimeString(sharedPrefExpert.getInt("SHORTEST_TIME", -1))
        findViewById<TextView>(R.id.totalGamesLostExpert).text =
            sharedPrefExpert.getInt("TOTAL_GAMES_LOST", 0).toString()
        findViewById<TextView>(R.id.totalGameWonExpert).text =
            sharedPrefExpert.getInt("TOTAL_GAMES_WON", 0).toString()
    }

    private fun setIntermediateModeStats() {
        val sharedPrefIntermediate = getSharedPreferences("INTERMEDIATE", MODE_PRIVATE)
        findViewById<TextView>(R.id.totalGamesIntermediate).text =
            sharedPrefIntermediate.getInt("TOTAL_GAMES_COUNT", 0).toString()
        findViewById<TextView>(R.id.totalTimeIntermediate).text =
            getTimeString(sharedPrefIntermediate.getInt("TOTAL_TIME", 0))
        findViewById<TextView>(R.id.shortestTimeIntermediate).text =
            getShortestTimeString(sharedPrefIntermediate.getInt("SHORTEST_TIME", -1))
        findViewById<TextView>(R.id.totalGamesLostIntermediate).text =
            sharedPrefIntermediate.getInt("TOTAL_GAMES_LOST", 0).toString()
        findViewById<TextView>(R.id.totalGameWonIntermediate).text =
            sharedPrefIntermediate.getInt("TOTAL_GAMES_WON", 0).toString()
    }

    private fun setBeginnerModeStats() {
        val sharedPrefBeginner = getSharedPreferences("BEGINNER", MODE_PRIVATE)
        findViewById<TextView>(R.id.totalGamesBeginner).text =
            sharedPrefBeginner.getInt("TOTAL_GAMES_COUNT", 0).toString()
        findViewById<TextView>(R.id.totalTimeBeginner).text =
            getTimeString(sharedPrefBeginner.getInt("TOTAL_TIME", 0))
        findViewById<TextView>(R.id.shortestTimeBeginner).text =
            getShortestTimeString(sharedPrefBeginner.getInt("SHORTEST_TIME", -1))
        findViewById<TextView>(R.id.totalGamesLostBeginner).text =
            sharedPrefBeginner.getInt("TOTAL_GAMES_LOST", 0).toString()
        findViewById<TextView>(R.id.totalGameWonBeginner).text =
            sharedPrefBeginner.getInt("TOTAL_GAMES_WON", 0).toString()
    }

    private fun setStandardModeStats() {
        val sharedPrefStandard = getSharedPreferences("STANDARD", MODE_PRIVATE)
        findViewById<TextView>(R.id.totalGamesStandard).text =
            sharedPrefStandard.getInt("TOTAL_GAMES_COUNT", 0).toString()
        findViewById<TextView>(R.id.totalTimeStandard).text =
            getTimeString(sharedPrefStandard.getInt("TOTAL_TIME", 0))
        findViewById<TextView>(R.id.shortestTimeStandard).text =
            getShortestTimeString(sharedPrefStandard.getInt("SHORTEST_TIME", -1))
        findViewById<TextView>(R.id.totalGamesLostStandard).text =
            sharedPrefStandard.getInt("TOTAL_GAMES_LOST", 0).toString()
        findViewById<TextView>(R.id.totalGameWonStandard).text =
            sharedPrefStandard.getInt("TOTAL_GAMES_WON", 0).toString()
    }

    private fun getShortestTimeString(secondsElapsed: Int): String {
        return if (secondsElapsed == -1) "-"
        else getTimeString(secondsElapsed)
    }

    private fun getTimeString(secondsElapsed: Int): String {
        val hours = secondsElapsed / 3600
        val minutes = (secondsElapsed % 3600) / 60
        val seconds = secondsElapsed % 60
        return if (hours == 0) {
            if (minutes == 0) {
                "${seconds}s"
            } else {
                "${minutes}m${seconds}s"
            }
        } else {
            "${hours}h${minutes}m${seconds}s"
        }
    }
}