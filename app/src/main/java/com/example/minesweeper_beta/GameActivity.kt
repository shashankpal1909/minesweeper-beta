package com.example.minesweeper_beta

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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

private var secondsElapsed = 0

var timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
    override fun onTick(p0: Long) {
    }

    override fun onFinish() {
    }
}

class GameActivity : AppCompatActivity() {
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

        val timerTextView = findViewById<TextView>(R.id.timerTextView)


        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                secondsElapsed += 1
                updateGameTime(secondsElapsed, timerTextView)
            }

            override fun onFinish() {
                Toast.makeText(applicationContext, "Game Over: Timer Expired", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun dpToPx(dp: Int): Int {
            val density: Float = this.resources.displayMetrics.density
            return (dp.toFloat() * density).roundToInt()
        }

        var flagCount = getMineCount(width, height, mines, false)
        val flagCountTextView = findViewById<TextView>(R.id.flagCountTextView)

        val boardLinearLayout = findViewById<LinearLayout>(R.id.boardLinearLayout)

        val mainMenuButton = findViewById<Button>(R.id.mainMenuGameButton)
        mainMenuButton.setOnClickListener {
            finish()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val shape = GradientDrawable()
        shape.cornerRadius = dpToPx(2).toFloat()

        fun updateBoard(
            width: Int,
            height: Int,
            newGameStarting: Boolean = false,
            gameContinue: Boolean = false
        ) {
            var counter = 1

            shape.setColor(Color.rgb(185, 185, 185))

            for (i in 0 until width) {
                for (j in 0 until height) {
                    val mineCell = minesweeper.board[i][j]
                    val cell = findViewById<TextView>(counter)
                    if (!mineCell.isFinal || gameContinue) {
                        if (mineCell.isRevealed) {
                            mineCell.isFinal = true
                            cell.background = AppCompatResources.getDrawable(
                                this,
                                R.drawable.revealed_cell_background
                            )
                            if (mineCell.value != 0) {
                                cell.text = mineCell.value.toString()
                            }

                        } else if (mineCell.isMarked) {
                            val mineCellShape = GradientDrawable()
                            mineCellShape.cornerRadius = dpToPx(2).toFloat()
                            mineCellShape.setColor(Color.rgb(54, 69, 79))
                            cell.background =
                                AppCompatResources.getDrawable(this, R.drawable.flag_background)
                            cell.text = "⚑"
                        } else if (minesweeper.status == Status.LOST && mineCell.value == -1) {
                            val mineCellShape = GradientDrawable()
                            mineCellShape.cornerRadius = dpToPx(2).toFloat()
                            mineCellShape.setColor(Color.rgb(219, 88, 96))
                            cell.background =
                                AppCompatResources.getDrawable(this, R.drawable.mine_background_red)
                            mineCell.isFinal = true
                            cell.text = "✹"
                        } else if (minesweeper.status == Status.WON && mineCell.value == -1) {
                            val mineCellShape = GradientDrawable()
                            mineCellShape.cornerRadius = dpToPx(2).toFloat()
                            mineCellShape.setColor(Color.rgb(86, 168, 105))
                            mineCell.isFinal = true
                            cell.background = AppCompatResources.getDrawable(
                                this,
                                R.drawable.mine_background_green
                            )
                            cell.text = "✹"
                        } else {
                            if (newGameStarting) {
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
                    counter++
                }
            }

            if ((minesweeper.status == Status.WON || minesweeper.status == Status.LOST) && !gameContinue) {

                timer.cancel()
                val sharedPrefLastGame =
                    getSharedPreferences("LAST_GAME_STATS", Context.MODE_PRIVATE)
                with(sharedPrefLastGame.edit()) {
                    putString(
                        "STATS",
                        "LAST GAME : ${getTimeString(secondsElapsed)} | ${boardName.uppercase()} | ${height}×${width} | ${
                            getMineCount(
                                width,
                                height,
                                mines,
                                false
                            )
                        }✹ | ${minesweeper.status}"
                    )
                    commit()
                }

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

                if (minesweeper.status == Status.LOST) {
                    with(sharedPrefGameStats) {
                        edit().putInt(
                            "TOTAL_GAMES_LOST",
                            getInt("TOTAL_GAMES_LOST", 0) + 1
                        ).apply()
                    }
                    Toast.makeText(this, "YOU LOST! Better Luck Next Time.", Toast.LENGTH_SHORT)
                        .show()
                } else if (minesweeper.status == Status.WON) {
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
                            getMineCount(
                                width,
                                height,
                                mines,
                                false
                            )
                        } Mines in ${getTimeString(secondsElapsed)} Seconds.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

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

        val restartGameButton = findViewById<Button>(R.id.restartGameButton)

        restartGameButton.setOnClickListener {
            minesweeper = Minesweeper(width, height)
            minesArray = MutableList(getMineCount(width, height, mines, false)) { IntArray(2) }
            flagCount = getMineCount(width, height, mines, false)
            flagCountTextView.text = getString(R.string.flag_count_text, flagCount)
            firstMove = true
            updateBoard(width, height, true)
        }


        val paramsCell = LinearLayout.LayoutParams(dpToPx(38), dpToPx(38))
        paramsCell.setMargins(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1))

        var counter = 1

        for (i in 0 until width) {

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params
            for (j in 0 until height) {

                val cell = TextView(this)
                cell.id = counter++
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


                fun handleCellClick(choice: Int = 1) {
                    if (firstMove) {
                        firstMove = false
                        timer.start()
                        setupMines(i, j)
                        println("Mines:${printMines(minesArray)}")
                    }
                    if (minesweeper.move(choice, i, j)) {
                        if (choice == 2) {
                            flagCount--
                            println("Flag $flagCount")
                        }
                        updateBoard(width, height)
                    }
                    println("Move $choice ${i + 1} ${j + 1}")
                }

                cell.setOnClickListener {
                    handleCellClick()
                }

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

        flagCountTextView.text = getString(R.string.flag_count_text, flagCount)

    }

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

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        getSharedPreferences("SAVE_GAME", Context.MODE_PRIVATE).edit()
            .putInt("GAME_TIME", secondsElapsed).apply()
        secondsElapsed = 0
    }

    private fun printMines(minesArray: MutableList<IntArray>): String {
        var str = ""
        minesArray.forEach { str += " (${it[0] + 1},${it[1] + 1})" }
        return str
    }

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


