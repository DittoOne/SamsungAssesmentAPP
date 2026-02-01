package com.example.internassessmentapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.math.*

class CalculatorViewModel : ViewModel() {
    var expression = mutableStateOf("")
    var result = mutableStateOf("0")

    private var lastWasOperator = false

    fun addNumber(number: String) {
        expression.value += number
        lastWasOperator = false
    }

    fun addOperator(operator: String) {
        if (expression.value.isEmpty() && operator != "-" && operator != "(") return

        // Prevent consecutive operators except for negative sign
        if (!lastWasOperator || operator == "-" || operator == "(") {
            expression.value += operator
            lastWasOperator = operator !in listOf(")", "^2")
        }
    }

    fun addFunction(function: String) {
        expression.value += function
        lastWasOperator = false
    }

    fun addDecimal() {
        if (expression.value.isEmpty()) {
            expression.value = "0."
            return
        }

        // Check if current number already has a decimal
        val lastNumber = expression.value.takeLastWhile { it.isDigit() || it == '.' }
        if (!lastNumber.contains('.')) {
            expression.value += "."
        }
        lastWasOperator = false
    }

    fun toggleSign() {
        if (expression.value.isEmpty()) return

        // Simple implementation: add/remove minus at start
        if (expression.value.startsWith("-")) {
            expression.value = expression.value.substring(1)
        } else {
            expression.value = "-" + expression.value
        }
    }

    fun clear() {
        expression.value = ""
        result.value = "0"
        lastWasOperator = false
    }

    fun calculate() {
        if (expression.value.isEmpty()) {
            result.value = "0"
            return
        }

        try {
            val calculationResult = evaluateExpression(expression.value)
            result.value = formatResult(calculationResult)
        } catch (e: Exception) {
            result.value = "Error"
        }
    }

    private fun evaluateExpression(expr: String): Double {
        var exp = expr.replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("√", "sqrt")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())

        // Handle scientific functions
        exp = handleFunctions(exp)

        // Evaluate the mathematical expression
        return evaluate(exp)
    }

    private fun handleFunctions(expr: String): String {
        var result = expr

        // Handle trigonometric functions
        result = replaceFunctionCalls(result, "sin") { sin(Math.toRadians(it)) }
        result = replaceFunctionCalls(result, "cos") { cos(Math.toRadians(it)) }
        result = replaceFunctionCalls(result, "tan") { tan(Math.toRadians(it)) }

        // Handle logarithms
        result = replaceFunctionCalls(result, "log") { log10(it) }
        result = replaceFunctionCalls(result, "ln") { ln(it) }

        // Handle square root
        result = replaceFunctionCalls(result, "sqrt") { sqrt(it) }

        return result
    }

    private fun replaceFunctionCalls(expr: String, funcName: String, func: (Double) -> Double): String {
        var result = expr
        val pattern = "$funcName\\(([^)]+)\\)".toRegex()

        while (pattern.containsMatchIn(result)) {
            result = pattern.replace(result) { matchResult ->
                val arg = matchResult.groupValues[1]
                val value = evaluate(arg)
                func(value).toString()
            }
        }

        return result
    }

    private fun evaluate(expr: String): Double {
        return ExpressionEvaluator().eval(expr)
    }

    private fun formatResult(value: Double): String {
        return when {
            value.isNaN() -> "Error"
            value.isInfinite() -> "∞"
            value % 1.0 == 0.0 -> value.toLong().toString()
            else -> String.format("%.8f", value).trimEnd('0').trimEnd('.')
        }
    }
}

/**
 * Simple expression evaluator that handles basic arithmetic and powers
 * Supports: +, -, *, /, ^, parentheses
 */
class ExpressionEvaluator {
    private var pos = 0
    private lateinit var expr: String

    fun eval(expression: String): Double {
        expr = expression.replace(" ", "")
        pos = 0
        return parseExpression()
    }

    private fun parseExpression(): Double {
        var result = parseTerm()

        while (pos < expr.length) {
            when {
                match('+') -> result += parseTerm()
                match('-') -> result -= parseTerm()
                else -> break
            }
        }

        return result
    }

    private fun parseTerm(): Double {
        var result = parseFactor()

        while (pos < expr.length) {
            when {
                match('*') -> result *= parseFactor()
                match('/') -> {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    result /= divisor
                }
                else -> break
            }
        }

        return result
    }

    private fun parseFactor(): Double {
        var result = parseBase()

        while (pos < expr.length && match('^')) {
            result = result.pow(parseBase())
        }

        return result
    }

    private fun parseBase(): Double {
        // Handle unary minus
        if (match('-')) {
            return -parseBase()
        }

        // Handle unary plus
        if (match('+')) {
            return parseBase()
        }

        // Handle parentheses
        if (match('(')) {
            val result = parseExpression()
            if (!match(')')) {
                throw IllegalArgumentException("Missing closing parenthesis")
            }
            return result
        }

        // Parse number
        val start = pos
        while (pos < expr.length && (expr[pos].isDigit() || expr[pos] == '.')) {
            pos++
        }

        if (start == pos) {
            throw IllegalArgumentException("Unexpected character at position $pos")
        }

        return expr.substring(start, pos).toDouble()
    }

    private fun match(char: Char): Boolean {
        if (pos < expr.length && expr[pos] == char) {
            pos++
            return true
        }
        return false
    }
}