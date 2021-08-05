package com.example.minesweeper_beta

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
import kotlin.math.roundToInt
import kotlin.random.Random


class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        var secondsElapsed = 0
        var seconds = 0
        var minutes = 0
        var hours = 0
        val timerTextView = findViewById<TextView>(R.id.timerTextView)

        var timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                secondsElapsed += 1
                hours = secondsElapsed / 3600
                minutes = (secondsElapsed % 3600) / 60
                seconds = secondsElapsed % 60

                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

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

        val width = intent.extras!!["WIDTH"].toString().toInt()
        val height = intent.extras!!["HEIGHT"].toString().toInt()
        val mines = intent.extras!!["MINES"].toString().toInt()

        var flagCount = getMineCount(width, height, mines, false)
        val flagCountTextView = findViewById<TextView>(R.id.flagCountTextView)
        flagCountTextView.text = "⚑ $flagCount"

        val boardLinearLayout = findViewById<LinearLayout>(R.id.boardLinearLayout)

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        mainMenuButton.setOnClickListener {
            finish()
        }


        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val shape = GradientDrawable()
        shape.cornerRadius = dpToPx(2).toFloat()

        var firstMove = true

        var minesweeper = Minesweeper(width, height)
        var minesArray = MutableList(getMineCount(width, height, mines, true)) { IntArray(2) }

        fun getTextColor(mineCell: MineCell) = when (mineCell.value) {
            1 -> Color.rgb(0, 0, 255)
            2 -> Color.rgb(0, 130, 0)
            3 -> Color.rgb(255, 0, 0)
            4 -> Color.rgb(0, 0, 132)
            5 -> Color.rgb(132, 0, 0)
            6 -> Color.rgb(0, 130, 132)
            7 -> Color.rgb(132, 0, 132)
            8 -> Color.rgb(117, 117, 117)
            else -> Color.BLACK
        }

        fun updateBoard(width: Int, height: Int, newGameStarting: Boolean = false) {
            var counter = 1

            shape.setColor(Color.rgb(185, 185, 185))

            for (i in 0 until width) {
                for (j in 0 until height) {
                    val mineCell = minesweeper.board[i][j]
                    val cell = findViewById<TextView>(counter)
                    if (!mineCell.isFinal) {
                        if (mineCell.isRevealed) {
                            mineCell.isFinal = true
                            cell.background = shape
                            if (mineCell.value != 0) {
                                cell.text = mineCell.value.toString()
                            }
                            cell.setTextColor(
                                getTextColor(mineCell)
                            )
                        } else if (mineCell.isMarked) {
                            val mineCellShape = GradientDrawable()
                            mineCellShape.cornerRadius = dpToPx(2).toFloat()
                            mineCellShape.setColor(Color.rgb(54, 69, 79))
                            cell.background = mineCellShape
                            cell.setTextColor(Color.WHITE)
                            cell.text = "⚑"
                        } else if (minesweeper.status == Status.LOST && mineCell.value == -1) {
                            val mineCellShape = GradientDrawable()
                            mineCellShape.cornerRadius = dpToPx(2).toFloat()
                            mineCellShape.setColor(Color.rgb(219, 88, 96))
                            cell.background = mineCellShape
                            mineCell.isFinal = true
                            cell.text = "✹"
                            cell.setTextColor(Color.WHITE)
                        } else if (minesweeper.status == Status.WON && mineCell.value == -1) {
                            val mineCellShape = GradientDrawable()
                            mineCellShape.cornerRadius = dpToPx(2).toFloat()
                            mineCellShape.setColor(Color.rgb(86, 168, 105))
                            mineCell.isFinal = true
                            cell.background = mineCellShape
                            cell.text = "✹"
                            cell.setTextColor(Color.WHITE)
                        } else {
                            if (newGameStarting) {
                                cell.text = ""
                                timer.cancel()
                                secondsElapsed = 0
                                timerTextView.text = String.format("%02d:%02d:%02d", 0, 0, 0)
                                cell.setBackgroundResource(R.drawable.rounded_corner_view)
                                cell.setTextColor(Color.BLACK)
                            }
                        }
                    }
                    counter++
                }
            }
            if (minesweeper.status == Status.LOST) {
                timer.cancel()
                Toast.makeText(this, "You Lost", Toast.LENGTH_SHORT).show()
            } else if (minesweeper.status == Status.WON) {
                timer.cancel()
                Toast.makeText(this, "You Won", Toast.LENGTH_SHORT).show()
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
            flagCountTextView.text = "⚑ $flagCount"
            firstMove = true
            updateBoard(width, height, true)
        }


        val paramsCell = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40))
        paramsCell.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))

        var counter = 1

        for (i in 0 until width) {

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params
            for (j in 0 until height) {

                val cell = TextView(this)
                cell.id = counter++
                cell.text = "" /*minesweeper.board[i][j].value.toString()*/
                cell.gravity = Gravity.CENTER
                cell.setBackgroundResource(R.drawable.rounded_corner_view)
                cell.setTextColor(Color.BLACK)
                cell.setTypeface(cell.typeface, Typeface.BOLD)
                cell.layoutParams = paramsCell
                cell.textSize = 24F

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
                    println("Move $choice $i $j")
                }

                cell.setOnClickListener {
                    handleCellClick()
                }

                cell.setOnLongClickListener {
                    if (flagCount > 0) {
                        handleCellClick(2)
                        flagCountTextView.text = "⚑ $flagCount"
                    }
                    return@setOnLongClickListener true
                }

                linearLayout.addView(cell)
            }
            boardLinearLayout.addView(linearLayout)
        }

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
                        "Mine Count cannot be more that 1/4th of Board Area. New Mine Count : ${width * height / 4}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                width * height / 4
            } else mines
        }
    }


}


