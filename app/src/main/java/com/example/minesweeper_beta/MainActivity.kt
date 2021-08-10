package com.example.minesweeper_beta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // GET REFERENCE TO BUTTONS
        val continueButton = findViewById<Button>(R.id.continueButton)
        val newGameButton = findViewById<Button>(R.id.newGameButton)
        val newGameLinearLayout = findViewById<LinearLayout>(R.id.newGameLinearLayout)
        val standardButton = findViewById<Button>(R.id.standardButton)
        val beginnerButton = findViewById<Button>(R.id.beginnerButton)
        val intermediateButton = findViewById<Button>(R.id.intermediateButton)
        val expertButton = findViewById<Button>(R.id.expertButton)
        val masterButton = findViewById<Button>(R.id.masterButton)
        val customButton = findViewById<Button>(R.id.customButton)
        val statsButton = findViewById<Button>(R.id.statsButton)

        // SET CONTINUE BUTTON'S VISIBILITY
        val sharedPrefSaveGame = getSharedPreferences("SAVE_GAME", Context.MODE_PRIVATE)
        if (sharedPrefSaveGame.getInt("GAME_TIME", -1) == -1) {
            continueButton.visibility = View.GONE
        }

        // CONTINUE BUTTON'S ON-CLICK LISTENER
        continueButton.setOnClickListener {
            startGame(
                sharedPrefSaveGame.getInt("WIDTH", 0),
                sharedPrefSaveGame.getInt("HEIGHT", 0),
                sharedPrefSaveGame.getInt("MINES", 0),
                sharedPrefSaveGame.getString("NAME", "").toString(),
                sharedPrefSaveGame.getInt("GAME_TIME", 0)
            )
        }

        // NEW GAME BUTTON'S ON-CLICK LISTENER
        newGameButton.setOnClickListener {
            newGameLinearLayout.visibility = View.VISIBLE
            newGameButton.visibility = View.GONE
        }

        // ON-CLICK LISTENERS FOR DIFFERENT BOARD BUTTONS
        standardButton.setOnClickListener { startGame(19, 10, 38, "Standard") }
        beginnerButton.setOnClickListener { startGame(9, 9, 10, "Beginner") }
        intermediateButton.setOnClickListener { startGame(16, 16, 40, "Intermediate") }
        expertButton.setOnClickListener { startGame(24, 24, 99, "Expert") }
        masterButton.setOnClickListener { startGame(50, 50, 300, "Master") }

        // CUSTOM BUTTON'S ON-CLICK LISTENER
        customButton.setOnClickListener {

            // BUILD A CUSTOM ALERT DIALOG TO GET CUSTOM BOARD'S DETAILS
            val builder = AlertDialog.Builder(this)
            val dialogView = View.inflate(this, R.layout.custom_dialog, null)

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

                    // VALIDATE DATA USER ENTERED BY USER
                    when {
                        width == "" || width == "0" -> showWarning("Please Enter a valid Row Count!")
                        height == "" || height == "0" -> showWarning("Please Enter a valid Column Count!")
                        mines == "" -> showWarning("Oops! You forgot to enter the Mine Count.")
                        mines == "0" -> showWarning("There should be at least one Mine! -_-")
                        else -> startGame(
                            widthEditText.text.toString().toInt(),
                            heightEditText.text.toString().toInt(),
                            minesEditText.text.toString().toInt(),
                            "Custom"
                        )
                    }
                }
                setNegativeButton("CANCEL") { _, _ -> }
            }

            // SHOW DIALOG TO USER
            val alertDialog = builder.create()
            alertDialog.show()
        }

        // STATISTICS BUTTON - ON-CLICK LISTENER
        statsButton.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }

        // GET LAST THEME MODE APP WAS USING AND APPLYING IT
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        println("THEME MODE: ${sharedPref.getInt("THEME", -1)}")
        when (sharedPref.getInt("THEME", -1)) {
            -1, 0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        // CHANGE THE BUTTON'S TEXT ON THE BASIS OF CURRENT THEME
        val changeThemeButton = findViewById<Button>(R.id.changeThemeButton)
        println("LAST MODE: ${AppCompatDelegate.getDefaultNightMode()}")
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> {
                changeThemeButton.text = getString(R.string.use_dark_theme_text)
            }
            else -> {
                changeThemeButton.text = getString(R.string.use_light_theme_text)
            }
        }

        // CHANGE THEME BUTTON - ON-CLICK LISTENER
        changeThemeButton.setOnClickListener {
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> {
                    sharedPref.edit().putInt("THEME", 1).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else -> {
                    sharedPref.edit().putInt("THEME", 0).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        // SET CONTINUE BUTTON'S VISIBILITY WHEN MAIN ACTIVITY IS RESUMED
        val sharedPrefSaveGame = getSharedPreferences("SAVE_GAME", Context.MODE_PRIVATE)
        val continueButton = findViewById<Button>(R.id.continueButton)
        if (sharedPrefSaveGame.getInt("GAME_TIME", -1) != -1) {
            continueButton.visibility = View.VISIBLE
        }

        // TOGGLES VISIBILITY OF NEW GAME BUTTON
        val newGameButton = findViewById<Button>(R.id.newGameButton)
        val newGameLinearLayout = findViewById<LinearLayout>(R.id.newGameLinearLayout)
        newGameLinearLayout.visibility = View.GONE
        newGameButton.visibility = View.VISIBLE

    }

    // TO SHOW A WARNING TOAST
    private fun showWarning(str: String) {
        Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
    }

    // STARTS THE GAME
    private fun startGame(
        width: Int,
        height: Int,
        mines: Int = -1,
        name: String,
        secondsElapsed: Int = -1
    ) {
        startActivity(Intent(this, GameActivity::class.java).apply {
            putExtra("WIDTH", width)
            putExtra("HEIGHT", height)
            putExtra("MINES", mines)
            putExtra("NAME", name)
            putExtra("GAME_TIME", secondsElapsed)
        })
    }

}