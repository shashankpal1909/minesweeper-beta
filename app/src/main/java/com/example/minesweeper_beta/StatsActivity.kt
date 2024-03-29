package com.example.minesweeper_beta

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class StatsActivity : AppCompatActivity() {

    // GENERAL STATS LIST
    private var generalStatsList = mutableListOf(0, 0, -1, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val mainMenuButton = findViewById<Button>(R.id.mainMenuStatsButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val lastGameStatsTextView = findViewById<TextView>(R.id.lastGameStatsTextView)

        // GET LAST GAME'S STATS AND DISPLAY THEM
        val sharedPrefLastGame = getSharedPreferences("LAST_GAME_STATS", Context.MODE_PRIVATE)
        setStats(lastGameStatsTextView, sharedPrefLastGame)

        // MAIN MENU BUTTON - ON-CLICK LISTENER
        mainMenuButton.setOnClickListener {
            finish()
        }

        // RESET BUTTON - ON-CLICK LISTENER
        resetButton.setOnClickListener {

            // BUILD A ALERT DIALOG TO GET USER'S CONFIRMATION
            val builder = AlertDialog.Builder(this)
            with(builder) {
                setTitle("Confirm Reset")
                setMessage("Reset All Game Statistics?")
                setPositiveButton("RESET") { _, _ ->
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
                    generalStatsList = mutableListOf(0, 0, -1, 0, 0)
                    setStats(lastGameStatsTextView, sharedPrefLastGame)
                }
                setNegativeButton("CANCEL") { _, _ -> }
            }
            val alertDialog = builder.create()
            alertDialog.show()

        }

    }

    // SETS GAME STATS
    private fun setStats(
        lastGameStatsTextView: TextView,
        sharedPrefLastGame: SharedPreferences
    ) {
        setLastGameStats(lastGameStatsTextView, sharedPrefLastGame)
        setGameStats()
        setGeneralStats(
            findViewById(R.id.totalGamesGeneral),
            findViewById(R.id.totalTimeGeneral),
            findViewById(R.id.shortestTimeGeneral),
            findViewById(R.id.totalGamesLostGeneral),
            findViewById(R.id.totalGameWonGeneral)
        )
    }

    // SETS GENERAL STATS
    private fun setGeneralStats(
        totalGamesTextView: TextView,
        totalTimeTextView: TextView,
        shortestTimeTextView: TextView,
        totalGamesLostTextView: TextView,
        totalGamesWonTextView: TextView
    ) {
        totalGamesTextView.text = generalStatsList[0].toString()
        totalTimeTextView.text = getTimeString(generalStatsList[1])
        shortestTimeTextView.text = getShortestTimeString(generalStatsList[2])
        totalGamesLostTextView.text = generalStatsList[3].toString()
        totalGamesWonTextView.text = generalStatsList[4].toString()
    }

    // SETS STATS FOR DIFFERENT MODES
    private fun setGameStats() {

        // SET STATS FOR STANDARD MODE
        setStats(
            "STANDARD",
            findViewById(R.id.totalGamesStandard),
            findViewById(R.id.totalTimeStandard),
            findViewById(R.id.shortestTimeStandard),
            findViewById(R.id.totalGamesLostStandard),
            findViewById(R.id.totalGameWonStandard)
        )

        // SET STATS FOR BEGINNER MODE
        setStats(
            "BEGINNER",
            findViewById(R.id.totalGamesBeginner),
            findViewById(R.id.totalTimeBeginner),
            findViewById(R.id.shortestTimeBeginner),
            findViewById(R.id.totalGamesLostBeginner),
            findViewById(R.id.totalGameWonBeginner)
        )

        // SET STATS FOR INTERMEDIATE MODE
        setStats(
            "INTERMEDIATE",
            findViewById(R.id.totalGamesIntermediate),
            findViewById(R.id.totalTimeIntermediate),
            findViewById(R.id.shortestTimeIntermediate),
            findViewById(R.id.totalGamesLostIntermediate),
            findViewById(R.id.totalGameWonIntermediate)
        )

        // SET STATS FOR EXPERT MODE
        setStats(
            "EXPERT",
            findViewById(R.id.totalGamesExpert),
            findViewById(R.id.totalTimeExpert),
            findViewById(R.id.shortestTimeExpert),
            findViewById(R.id.totalGamesLostExpert),
            findViewById(R.id.totalGameWonExpert)
        )

        // SET STATS FOR MASTER MODE
        setStats(
            "MASTER",
            findViewById(R.id.totalGamesMaster),
            findViewById(R.id.totalTimeMaster),
            findViewById(R.id.shortestTimeMaster),
            findViewById(R.id.totalGamesLostMaster),
            findViewById(R.id.totalGameWonMaster)
        )

        // SET STATS FOR CUSTOM MODE
        setStats(
            "CUSTOM",
            findViewById(R.id.totalGamesCustom),
            findViewById(R.id.totalTimeCustom),
            findViewById(R.id.shortestTimeCustom),
            findViewById(R.id.totalGamesLostCustom),
            findViewById(R.id.totalGameWonCustom)
        )

    }

    // SETS LAST GAME STATS
    private fun setLastGameStats(
        lastGameStatsTextView: TextView,
        sharedPrefLastGame: SharedPreferences
    ) {
        lastGameStatsTextView.text = sharedPrefLastGame.getString("STATS", "NO GAMES PLAYED YET!")
    }

    // SETS STATS
    private fun setStats(
        mode: String,
        totalGamesTextView: TextView,
        totalTimeTextView: TextView,
        shortestTimeTextView: TextView,
        totalGamesLostTextView: TextView,
        totalGamesWonTextView: TextView
    ) {
        val sharedPref = getSharedPreferences(mode, MODE_PRIVATE)

        val statsList = listOf(
            sharedPref.getInt("TOTAL_GAMES_COUNT", 0),
            sharedPref.getInt("TOTAL_TIME", 0),
            sharedPref.getInt("SHORTEST_TIME", -1),
            sharedPref.getInt("TOTAL_GAMES_LOST", 0),
            sharedPref.getInt("TOTAL_GAMES_WON", 0)
        )

        totalGamesTextView.text = statsList[0].toString()
        totalTimeTextView.text = getTimeString(statsList[1])
        shortestTimeTextView.text = getShortestTimeString(statsList[2])
        totalGamesLostTextView.text = statsList[3].toString()
        totalGamesWonTextView.text = statsList[4].toString()

        for (i in statsList.indices) {
            if (i == 2) continue
            generalStatsList[i] += statsList[i]
        }

        if ((generalStatsList[2] > statsList[2] || generalStatsList[2] == -1) && statsList[2] != -1)
            generalStatsList[2] = statsList[2]

    }

    // RETURNS FORMATTED TIME STRING
    private fun getShortestTimeString(secondsElapsed: Int): String {
        return if (secondsElapsed == -1) "-"
        else getTimeString(secondsElapsed)
    }

    // RETURNS FORMATTED TIME STRING
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