package com.example.minesweeper_beta

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.TextViewCompat
import com.google.gson.Gson
import kotlin.math.roundToInt
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private var secondsElapsed = 0
    lateinit var timerTextView: TextView

    // TIMER TO RECORD GAME TIME
    private var timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            secondsElapsed += 1
            updateGameTime(secondsElapsed, timerTextView)
        }

        override fun onFinish() {
            Toast.makeText(applicationContext, "Timer Expired", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val width = intent.extras!!["WIDTH"].toString().toInt()
        val height = intent.extras!!["HEIGHT"].toString().toInt()
        val mines = intent.extras!!["MINES"].toString().toInt()
        val boardName = intent.extras!!["NAME"].toString()

        var firstMove = true

        var minesweeper = Minesweeper(width, height)
        var minesArray = MutableList(getMineCount(width, height, mines, true)) { IntArray(2) }

        timerTextView = findViewById(R.id.timerTextView)

        var flagCount = getMineCount(width, height, mines, false)
        val flagCountTextView = findViewById<TextView>(R.id.flagCountTextView)

        val boardLinearLayout = findViewById<LinearLayout>(R.id.boardLinearLayout)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuGameButton)
        val restartGameButton = findViewById<Button>(R.id.restartGameButton)

        // CONVERTS DP TO PX
        fun dpToPx(dp: Int): Int {
            val density: Float = this.resources.displayMetrics.density
            return (dp.toFloat() * density).roundToInt()
        }

        // SETUP MINES ON RANDOM LOCATIONS (OTHER THAN FIRST MOVE'S LOCATION)
        fun setupMines(x: Int, y: Int) {
            for (i in 0 until getMineCount(width, height, mines, false)) {
                while (true) {
                    val row = Random.nextInt(0, width)
                    val column = Random.nextInt(0, height)
                    if ((row == x && column == y)) {
                        continue
                    }
                    if (minesweeper.setMine(row, column)) {
                        minesArray.add(intArrayOf(row, column))
                        break
                    }
                }
            }
        }

        // UPDATES THE BOARD
        fun updateBoard(
            width: Int,
            height: Int,
            newGameStarting: Boolean = false,
            gameContinue: Boolean = false
        ) {

            // CELL ID
            var cellID = 1

            // UPDATES REVEALED CELLS
            for (i in 0 until width) {
                for (j in 0 until height) {
                    val mineCell = minesweeper.board[i][j]
                    val cell = findViewById<TextView>(cellID)
                    when {
                        !mineCell.isFinal || gameContinue ->

                            when {

                                // IF CELL IS REVEALED THEN DISPLAY THE NUMBERS ON CELLS
                                mineCell.isRevealed -> {
                                    mineCell.isFinal = true
                                    cell.background = AppCompatResources.getDrawable(
                                        this,
                                        R.drawable.revealed_cell_background
                                    )
                                    when {
                                        mineCell.value != 0 -> cell.text = mineCell.value.toString()
                                    }

                                }

                                // IF CELL IS FLAGGED THEN DISPLAY FLAG ON CELL
                                mineCell.isMarked -> {
                                    cell.background =
                                        AppCompatResources.getDrawable(
                                            this,
                                            R.drawable.flag_background
                                        )
                                    cell.text = "⚑"
                                }

                                // IF USER HAS LOST THE GAME THEN REVEAL ALL MINES
                                minesweeper.status == Status.LOST && mineCell.value == -1 -> {
                                    cell.background =
                                        AppCompatResources.getDrawable(
                                            this,
                                            R.drawable.mine_background_red
                                        )
                                    mineCell.isFinal = true
                                    cell.text = "✹"
                                }

                                // IF USER HAS WON THE GAME THEN REVEAL ALL NON-FLAGGED MINES WITH GREEN BACKGROUND
                                minesweeper.status == Status.WON && mineCell.value == -1 -> {
                                    mineCell.isFinal = true
                                    cell.background = AppCompatResources.getDrawable(
                                        this,
                                        R.drawable.mine_background_green
                                    )
                                    cell.text = "✹"
                                }

                                else -> {

                                    when {

                                        // RESET THE DISPLAYED BOARD IF NEW GAME IS STARTED
                                        newGameStarting -> {
                                            cell.text = ""
                                            timer.cancel()
                                            secondsElapsed = 0
                                            timerTextView.text = String.format("%02d:%02d", 0, 0)
                                            cell.setBackgroundResource(R.drawable.rounded_corner_cell)
                                            TextViewCompat.setTextAppearance(
                                                cell,
                                                android.R.style.TextAppearance_Material_Body1
                                            )
                                            cell.setTypeface(cell.typeface, Typeface.BOLD)
                                            cell.textSize = 20F
                                        }

                                    }
                                }

                            }
                    }
                    cellID++
                }
            }

            // DISPLAY GAME RESULT AS TOAST AND SAVE GAME STATS IF GAME IS OVER
            if ((minesweeper.status == Status.WON || minesweeper.status == Status.LOST) && !gameContinue) {

                // STOP THE TIMER
                timer.cancel()

                // SAVE STATS AS LAST GAME STATS
                val sharedPrefLastGame =
                    getSharedPreferences("LAST_GAME_STATS", Context.MODE_PRIVATE)
                with(sharedPrefLastGame.edit()) {
                    putString(
                        "STATS",
                        "LAST GAME : ${getTimeString(secondsElapsed)} | ${boardName.uppercase()} | ${height}×${width} | " + "${
                            getMineCount(width, height, mines, false)
                        }✹ | ${minesweeper.status}"
                    )
                    commit()
                }

                // SAVE STATS AS MODE STATS
                val sharedPrefGameStats =
                    getSharedPreferences(boardName.uppercase(), Context.MODE_PRIVATE)
                with(sharedPrefGameStats) {
                    edit().putInt(
                        "TOTAL_GAMES_COUNT",
                        getInt("TOTAL_GAMES_COUNT", 0) + 1
                    ).apply()
                    edit().putInt(
                        "TOTAL_TIME",
                        getInt("TOTAL_TIME", 0) + secondsElapsed
                    ).apply()
                }

                // DISPLAY TOAST IF GAME IS LOST
                if (minesweeper.status == Status.LOST) {
                    with(sharedPrefGameStats) {
                        edit().putInt(
                            "TOTAL_GAMES_LOST",
                            getInt("TOTAL_GAMES_LOST", 0) + 1
                        ).apply()
                    }
                    Toast.makeText(this, "YOU LOST! Better Luck Next Time.", Toast.LENGTH_SHORT)
                        .show()
                }

                // DISPLAY TOAST IF GAME IS WON
                else if (minesweeper.status == Status.WON) {

                    // CHECK AND UPDATE THE SHORTEST TIME
                    with(sharedPrefGameStats) {
                        edit().putInt(
                            "TOTAL_GAMES_WON",
                            getInt("TOTAL_GAMES_WON", 0) + 1
                        ).apply()
                        if (sharedPrefGameStats.getInt(
                                "SHORTEST_TIME",
                                Int.MAX_VALUE
                            ) > secondsElapsed
                        ) {
                            edit().putInt("SHORTEST_TIME", secondsElapsed).apply()
                        }
                    }

                    Toast.makeText(
                        this, "YOU WON! Found ${
                            getMineCount(width, height, mines, false)
                        } Mines in ${getTimeString(secondsElapsed)} Seconds.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            // SAVE CURRENT GAME STATUS FOR CONTINUING THE GAME ON NEXT RUN
            val sharedPrefSaveGame = getSharedPreferences("SAVE_GAME", Context.MODE_PRIVATE)
            with(sharedPrefSaveGame.edit()) {
                putInt("GAME_TIME", secondsElapsed)
                putStringSet("BOARD", minesweeper.board.map { Gson().toJson(it) }.toSet())
                putString("BOARD", Gson().toJson(minesweeper.board))
                putString("NAME", boardName)
                putInt("WIDTH", width)
                putInt("HEIGHT", height)
                putInt("MINES", getMineCount(width, height, mines, false))
                putInt("FLAG_COUNT", flagCount)
                putString("STATUS", minesweeper.status.toString())
                putBoolean("FIRST_MOVE", firstMove)
                commit()
            }

        }

        // MAIN MENU BUTTON - ON-CLICK LISTENER
        mainMenuButton.setOnClickListener { finish() }

        // RESTART GAME BUTTON - ON-CLICK LISTENER
        restartGameButton.setOnClickListener {
            minesweeper = Minesweeper(width, height)
            minesArray = MutableList(getMineCount(width, height, mines, false)) { IntArray(2) }
            flagCount = getMineCount(width, height, mines, false)
            flagCountTextView.text = getString(R.string.flag_count_text, flagCount)
            firstMove = true
            updateBoard(width, height, true)
        }

        // HORIZONTAL LINEAR LAYOUT'S PARAMS
        val paramsLinearLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // CELL'S PARAMS
        val paramsCell = LinearLayout.LayoutParams(dpToPx(38), dpToPx(38))
        paramsCell.setMargins(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1))

        // CELL ID
        var cellId = 1

        // GENERATE BOARD ACCORDING TO WIDTH & HEIGHT
        for (i in 0 until width) {

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = paramsLinearLayout

            for (j in 0 until height) {

                val cell = TextView(this)
                cell.id = cellId++
                cell.text = ""
                cell.gravity = Gravity.CENTER
                cell.setBackgroundResource(R.drawable.rounded_corner_cell)
                TextViewCompat.setTextAppearance(
                    cell,
                    android.R.style.TextAppearance_Material_Body1
                )
                cell.setTypeface(cell.typeface, Typeface.BOLD)
                cell.textSize = 20F
                cell.layoutParams = paramsCell

                // HANDLES CELL CLICK EVENT
                fun handleCellClick(choice: Int = 1) {
                    if (firstMove) {
                        firstMove = false
                        timer.start()
                        setupMines(i, j)
                        println("Mines:${printMines(minesArray)}") // FOR DEBUGGING
                    }
                    if (minesweeper.move(choice, i, j)) {
                        if (choice == 2) {
                            flagCount--
                            println("Flag $flagCount") // FOR DEBUGGING
                        }
                        updateBoard(width, height)
                    }
                    println("Move $choice ${i + 1} ${j + 1}") // FOR DEBUGGING
                }

                // CELL'S ON-CLICK LISTENER (TO REVEAL THE CELL)
                cell.setOnClickListener { handleCellClick() }

                // CELL'S LONG-CLICK LISTENER (TO FLAG THE CELL)
                cell.setOnLongClickListener {
                    if (flagCount > 0) {
                        handleCellClick(2)
                        flagCountTextView.text = getString(R.string.flag_count_text, flagCount)
                    }
                    return@setOnLongClickListener true
                }

                linearLayout.addView(cell)

            }

            boardLinearLayout.addView(linearLayout)

        }

        // RESUME LAST STATE OF GAME IF USER CLICKED CONTINUE
        if (intent.extras!!["GAME_TIME"].toString().toInt() != -1) {

            secondsElapsed = intent.extras!!["GAME_TIME"].toString().toInt()
            updateGameTime(secondsElapsed, timerTextView)
            val sharedPrefSaveGame = getSharedPreferences("SAVE_GAME", Context.MODE_PRIVATE)
            minesweeper.board = Gson().fromJson(
                sharedPrefSaveGame.getString("BOARD", null),
                Array<Array<MineCell>>::class.java
            )
            flagCount = sharedPrefSaveGame.getInt("FLAG_COUNT", 0)
            firstMove = sharedPrefSaveGame.getBoolean("FIRST_MOVE", false)
            minesweeper.status = when (sharedPrefSaveGame.getString("STATUS", "")) {
                "WON" -> Status.WON
                "LOST" -> Status.LOST
                else -> {
                    if (!firstMove) timer.start()
                    Status.ONGOING
                }
            }
            updateBoard(width, height, gameContinue = true)
        }

        // DISPLAY FLAG COUNT
        flagCountTextView.text = getString(R.string.flag_count_text, flagCount)

    }

    // SAVES THE GAME TIME BEFORE ACTIVITY IS CLOSED
    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        getSharedPreferences("SAVE_GAME", Context.MODE_PRIVATE).edit()
            .putInt("GAME_TIME", secondsElapsed).apply()
        secondsElapsed = 0
    }

    // UPDATES THE GAME TIMER TEXTVIEW
    private fun updateGameTime(secondsElapsed: Int, timerTextView: TextView) {
        val hours = secondsElapsed / 3600
        val minutes = (secondsElapsed % 3600) / 60
        val seconds = secondsElapsed % 60
        timerTextView.text = when (hours) {
            0 -> {
                String.format("%02d:%02d", minutes, seconds)
            }
            else -> String.format("%02d:%02d:%02", hours, minutes, seconds)
        }
    }

    // RETURNS STRING TO PRINT MINE LOCATION (FOR DEBUGGING)
    private fun printMines(minesArray: MutableList<IntArray>): String {
        var str = ""
        minesArray.sortBy { it[0] }
        minesArray.forEach { str += " (${it[0] + 1},${it[1] + 1})" }
        return str
    }

    // GET A VALID MINE COUNT (IN CASE OF CUSTOM BOARD)
    private fun getMineCount(width: Int, height: Int, mines: Int, mineCountWarning: Boolean): Int {
        return if (mines == -1) {
            if ((width * height) / 8 == 0) {
                if ((width * height) / 4 == 0) 1 else width * height / 4
            } else width * height / 8
        } else {
            if (mines > (width * height) / 4) {
                if (mineCountWarning) {
                    Toast.makeText(
                        this@GameActivity,
                        "Mine Count cannot be more than 1/4th of Board Area. New Mine Count : ${width * height / 4}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                width * height / 4
            } else mines
        }
    }

    // GET A FORMATTED TIME STRING
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


