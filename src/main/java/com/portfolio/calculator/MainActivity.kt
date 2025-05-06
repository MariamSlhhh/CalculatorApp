package com.portfolio.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var workingTV: TextView
    private lateinit var resultsTV: TextView

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingTV = findViewById(R.id.workingTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    workingTV.append(".")
                    canAddDecimal = false
                }
            } else {
                workingTV.append(view.text)
            }

            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubCalc(timesDivision)
        return result.toString()
    }

    private fun addSubCalc(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        var i = 1
        while (i < passedList.size) {
            val operator = passedList[i] as String
            val nextDigit = passedList[i + 1] as Float

            when (operator) {
                "+" -> result += nextDigit
                "-" -> result -= nextDigit
            }
            i += 2
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains("x") || list.contains("/")) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()

        var i = 0
        while (i < passedList.size) {
            if (passedList[i] is String && i > 0 && i < passedList.lastIndex) {
                val operator = passedList[i] as String
                val prev = passedList[i - 1] as Float
                val next = passedList[i + 1] as Float

                if (operator == "x" || operator == "/") {
                    val result = if (operator == "x") prev * next else prev / next
                    newList.removeAt(newList.lastIndex) // Remove last added number
                    newList.add(result)
                    i += 2 // Skip the next number
                    continue
                }
            }
            newList.add(passedList[i])
            i++
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in workingTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character.toString()) // FIX: convert operator char to String
            }
        }

        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }

        return list
    }

    fun backSpaceAction(view: View) {
        val length = workingTV.length()
        if (length > 0)
            workingTV.text = workingTV.text.subSequence(0, length - 1)
    }

    fun allClearAction(view: View) {
        workingTV.text = ""
        resultsTV.text = ""
    }
}
