package com.dicerolling.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var diceSelectorSpinner: Spinner
    private lateinit var customSidesEditText: EditText
    private lateinit var rollButton: Button
    private lateinit var rollTwiceButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var storedValuesListView: ListView

    private val PREFS_KEY = "DiceRollingPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

        diceSelectorSpinner = findViewById(R.id.diceSelectorSpinner)
        customSidesEditText = findViewById(R.id.customSidesEditText)
        rollButton = findViewById(R.id.rollButton)
        rollTwiceButton = findViewById(R.id.rollTwiceButton)
        resultTextView = findViewById(R.id.resultTextView)
        storedValuesListView = findViewById(R.id.storedValuesListView)

        val diceOptions = resources.getStringArray(R.array.dice_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        diceSelectorSpinner.adapter = adapter

        rollButton.setOnClickListener {
            rollDice()
        }

        rollTwiceButton.setOnClickListener {
            rollDiceTwice()
        }

        val storedValuesList = getStoredValuesList()
        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, storedValuesList)
        storedValuesListView.adapter = listAdapter
    }

    private fun rollDice() {
        val selectedDiceOption = diceSelectorSpinner.selectedItem.toString()
        val result = if (selectedDiceOption == "Custom") {
            val customSides = customSidesEditText.text.toString().toIntOrNull() ?: 6
            rollCustomDice(customSides)
        } else {
            roll(selectedDiceOption)
        }
        resultTextView.text = "Roll Result: $result"
        saveRolledValue(result.toString())
    }

    private fun rollDiceTwice() {
        val selectedDiceOption = diceSelectorSpinner.selectedItem.toString()
        val result1 = if (selectedDiceOption == "Custom") {
            val customSides = customSidesEditText.text.toString().toIntOrNull() ?: 6
            rollCustomDice(customSides)
        } else {
            roll(selectedDiceOption)
        }

        val result2 = if (selectedDiceOption == "Custom") {
            val customSides = customSidesEditText.text.toString().toIntOrNull() ?: 6
            rollCustomDice(customSides)
        } else {
            roll(selectedDiceOption)
        }

        resultTextView.text = "Roll Result 1: $result1  Roll Result 2: $result2"
        saveRolledValue("Result 1:"+result1.toString()+"   Result 2: "+result2.toString())
    }

    private fun saveRolledValue(value: String) {
        val storedValues = getStoredValuesList().toMutableList()
        storedValues.add(value)
        saveValuesToSharedPreferences(storedValues)
    }

    private fun saveRolledValues(values: List<String>) {
        val rollValues = values.mapIndexed { index, value -> "Roll ${index + 1}: $value" }
        saveValuesToSharedPreferences(rollValues)
    }

    private fun saveValuesToSharedPreferences(values: List<String>) {
        val editor = sharedPreferences.edit()
        val valuesString = values.joinToString(", ")
        editor.putString("enteredValues", valuesString)
        editor.apply()

        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, values)
        storedValuesListView.adapter = listAdapter
    }

    private fun rollCustomDice(sides: Int): Int {
        return Random.nextInt(1, sides + 1)
    }

    private fun roll(diceType: String): Int {
        val sides = diceType.removePrefix("D").toInt()
        return Random.nextInt(1, sides + 1)
    }

    private fun getStoredValuesList(): List<String> {
        val storedValuesString = sharedPreferences.getString("enteredValues", "")
        return storedValuesString?.split(", ") ?: emptyList()
    }
}
