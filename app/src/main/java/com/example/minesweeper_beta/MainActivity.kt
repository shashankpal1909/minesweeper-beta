package com.example.minesweeper_beta

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newGameButton = findViewById<Button>(R.id.newGameButton)
        val newGameLinearLayout = findViewById<LinearLayout>(R.id.newGameLinearLayout)
        newGameButton.setOnClickListener {
            newGameLinearLayout.visibility = View.VISIBLE
            newGameButton.visibility = View.GONE
        }
        val beginnerButton = findViewById<Button>(R.id.beginnerButton)
        val intermediateButton = findViewById<Button>(R.id.intermediateButton)
        val expertButton = findViewById<Button>(R.id.expertButton)
        val customButton = findViewById<Button>(R.id.customButton)

        beginnerButton.setOnClickListener { startGame(9, 9) }

        intermediateButton.setOnClickListener { startGame(16, 16) }

        expertButton.setOnClickListener { startGame(30, 30) }

        customButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.custom_dialog, null)

            val widthEditText = dialogView.findViewById<EditText>(R.id.widthEditText)
            val heightEditText = dialogView.findViewById<EditText>(R.id.heightEditText)
            val minesEditText = dialogView.findViewById<EditText>(R.id.minesEditText)

            with(builder) {
                setView(dialogView)
                setTitle("Custom Board")
                setPositiveButton("START") { _, _ ->
                    val width = widthEditText.text.toString()
                    val height = heightEditText.text.toString()
                    val mines = minesEditText.text.toString()
                    when {
                        width == "" -> showWarning("Width")
                        height == "" -> showWarning("Height")
                        mines == "" -> showWarning("Mines")
                        else -> startGame(
                            widthEditText.text.toString().toInt(),
                            heightEditText.text.toString().toInt(),
                            minesEditText.text.toString().toInt()
                        )
                    }
                }
                setNegativeButton("CANCEL") { _, _ -> /*startActivity(Intent(this@MainActivity, MainActivity::class.java))*/ }
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }

    }

    private fun showWarning(str: String) {
        Toast.makeText(this@MainActivity, "Enter Valid $str", Toast.LENGTH_SHORT).show()
    }

    private fun startGame(width: Int, height: Int, mines: Int = -1) {
        startActivity(Intent(this, GameActivity::class.java).apply {
            putExtra("WIDTH", width)
            putExtra("HEIGHT", height)
            putExtra("MINES", mines)
        })
    }

}