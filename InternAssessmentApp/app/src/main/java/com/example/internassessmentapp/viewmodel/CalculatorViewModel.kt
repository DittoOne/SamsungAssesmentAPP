package com.example.internassessmentapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.math.*

data class CalculationHistory(
    val expression: String,
    val result: String
)

class CalculatorViewModel : ViewModel() {
    var expression = mutableStateOf("")
    var result = mutableStateOf("0")
    var cursorPosition = mutableStateOf(0)
    var history = mutableStateOf<List<CalculationHistory>>(emptyList())

    private var lastWasOperator = false

    fun addNumber(number: String) {
        val currentExpr = expression.value
        expression.value = currentExpr.substring(0, cursorPosition.value) +
                number +
                currentExpr.substring(cursorPosition.value)
        cursorPosition.value += number.length
        lastWasOperator = false
    }

    fun addOperator(operator: String) {
        if (expression.value.isEmpty() && operator != "-" && operator != "(") return

        val currentExpr = expression.value
        // Prevent consecutive operators except for negative sign
        if (!lastWasOperator || operator == "-" || operator == "(") {
            expression.value = currentExpr.substring(0, cursorPosition.value) +
                    operator +
                    currentExpr.substring(cursorPosition.value)
            cursorPosition.value += operator.length
            lastWasOperator = operator !in listOf(")", "²", "!", "%")
        }
    }

    fun addFunction(function: String) {
        val currentExpr = expression.value
        expression.value = currentExpr.substring(0, cursorPosition.value) +
                function +
                currentExpr.substring(cursorPosition.value)
        cursorPosition.value += function.length
        lastWasOperator = false
    }

    /**
     * Smart square root that doesn't require brackets for single numbers
     * but adds brackets for expressions
     */
    fun addSmartSquareRoot() {
        val currentExpr = expression.value
        val beforeCursor = currentExpr.substring(0, cursorPosition.value)

        // Check if there's a number immediately before the cursor
        val numberRegex = "(\\d+\\.?\\d*)$".toRegex()
        val match = numberRegex.find(beforeCursor)

        if (match != null) {
            // There's a number before cursor - wrap it in √()
            val number = match.value
            val startPos = match.range.first
            val afterCursor = currentExpr.substring(cursorPosition.value)

            expression.value = currentExpr.substring(0, startPos) +
                    "√($number)" +
                    afterCursor
            cursorPosition.value = startPos + "√($number)".length
        } else {
            // No number before cursor - add √( and let user input
            expression.value = currentExpr.substring(0, cursorPosition.value) +
                    "√(" +
                    currentExpr.substring(cursorPosition.value)
            cursorPosition.value += 2
        }
        lastWasOperator = false
    }

    /**
     * Add square (x²) to the last number or entire expression
     */
    fun addSquare() {
        val currentExpr = expression.value
        if (currentExpr.isEmpty()) return

        val beforeCursor = currentExpr.substring(0, cursorPosition.value)
        val afterCursor = currentExpr.substring(cursorPosition.value)

        // Check if there's a number immediately before the cursor
        val numberRegex = "(\\d+\\.?\\d*)$".toRegex()
        val match = numberRegex.find(beforeCursor)

        if (match != null) {
            // There's a number before cursor - add ² after it
            expression.value = beforeCursor + "²" + afterCursor
            cursorPosition.value += 1
        } else {
            // No number, just add ² (will be handled as operator)
            expression.value = beforeCursor + "²" + afterCursor
            cursorPosition.value += 1
        }
        lastWasOperator = false
    }

    /**
     * Add reciprocal (1/x) - converts current number to its reciprocal operation
     * If 5 is present, pressing 1/x should show 5/ waiting for next input
     */
    fun addReciprocal() {
        val currentExpr = expression.value

        // If expression is just a number, add / after it
        if (currentExpr.matches("\\d+\\.?\\d*".toRegex())) {
            expression.value = currentExpr + "/"
            cursorPosition.value = expression.value.length
            lastWasOperator = true
            return
        }

        val beforeCursor = currentExpr.substring(0, cursorPosition.value)
        val afterCursor = currentExpr.substring(cursorPosition.value)

        // Check if there's a number immediately before the cursor
        val numberRegex = "(\\d+\\.?\\d*)$".toRegex()
        val match = numberRegex.find(beforeCursor)

        if (match != null) {
            // There's a number before cursor - add / after it
            expression.value = beforeCursor + "/" + afterCursor
            cursorPosition.value += 1
            lastWasOperator = true
        } else {
            // No number found, just add division operator
            addOperator("/")
        }
    }

    fun addDecimal() {
        if (expression.value.isEmpty()) {
            expression.value = "0."
            cursorPosition.value = 2
            return
        }

        val currentExpr = expression.value
        val beforeCursor = currentExpr.substring(0, cursorPosition.value)

        // Check if current number already has a decimal
        val lastNumber = beforeCursor.takeLastWhile { it.isDigit() || it == '.' }
        if (!lastNumber.contains('.')) {
            expression.value = beforeCursor + "." + currentExpr.substring(cursorPosition.value)
            cursorPosition.value++
        }
        lastWasOperator = false
    }

    fun toggleSign() {
        if (expression.value.isEmpty()) return

        // Simple implementation: add/remove minus at start
        if (expression.value.startsWith("-")) {
            expression.value = expression.value.substring(1)
            if (cursorPosition.value > 0) {
                cursorPosition.value--
            }
        } else {
            expression.value = "-" + expression.value
            cursorPosition.value++
        }
    }

    fun backspace() {
        if (expression.value.isEmpty() || cursorPosition.value == 0) return

        val currentExpr = expression.value
        expression.value = currentExpr.substring(0, cursorPosition.value - 1) +
                currentExpr.substring(cursorPosition.value)
        cursorPosition.value--

        // Update lastWasOperator based on character before cursor
        if (cursorPosition.value > 0) {
            val charBeforeCursor = expression.value[cursorPosition.value - 1]
            lastWasOperator = charBeforeCursor in "+-×÷^%"
        }
    }

    fun clear() {
        expression.value = ""
        result.value = "0"
        cursorPosition.value = 0
        lastWasOperator = false
    }

    fun clearHistory() {
        history.value = emptyList()
    }

    fun moveCursorLeft() {
        if (cursorPosition.value > 0) {
            cursorPosition.value--
        }
    }

    fun moveCursorRight() {
        if (cursorPosition.value < expression.value.length) {
            cursorPosition.value++
        }
    }

    fun setCursorPosition(position: Int) {
        cursorPosition.value = position.coerceIn(0, expression.value.length)
    }

    /**
     * Load result from history to continue calculation
     * After hitting =, user can only work with the result
     */
    fun loadFromHistory(resultValue: String) {
        expression.value = resultValue
        cursorPosition.value = resultValue.length
        // Keep the result displayed
        result.value = resultValue
    }

    fun calculate() {
        if (expression.value.isEmpty()) {
            result.value = "0"
            return
        }

        try {
            val calculationResult = evaluateExpression(expression.value)
            val formattedResult = formatResult(calculationResult)
            result.value = formattedResult

            // Add to history
            val newHistory = history.value.toMutableList()
            newHistory.add(0, CalculationHistory(expression.value, formattedResult))
            if (newHistory.size > 50) { // Keep only last 50 calculations
                newHistory.removeAt(newHistory.size - 1)
            }
            history.value = newHistory

            // After calculation, load the result for next operation
            loadFromHistory(formattedResult)

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

        // Handle x² (square)
        exp = handleSquare(exp)

        // Handle percentage
        exp = handlePercentage(exp)

        // Handle factorial
        exp = handleFactorial(exp)

        // Handle scientific functions
        exp = handleFunctions(exp)

        // Evaluate the mathematical expression
        return evaluate(exp)
    }

    private fun handleSquare(expr: String): String {
        var result = expr
        val pattern = "(\\d+\\.?\\d*)²".toRegex()

        while (pattern.containsMatchIn(result)) {
            result = pattern.replace(result) { matchResult ->
                val value = matchResult.groupValues[1].toDouble()
                (value * value).toString()
            }
        }

        return result
    }

    private fun handlePercentage(expr: String): String {
        var result = expr
        val pattern = "(\\d+\\.?\\d*)%".toRegex()

        while (pattern.containsMatchIn(result)) {
            result = pattern.replace(result) { matchResult ->
                val value = matchResult.groupValues[1].toDouble()
                (value / 100.0).toString()
            }
        }

        return result
    }

    private fun handleFactorial(expr: String): String {
        var result = expr
        val pattern = "(\\d+)!".toRegex()

        while (pattern.containsMatchIn(result)) {
            result = pattern.replace(result) { matchResult ->
                val n = matchResult.groupValues[1].toInt()
                factorial(n).toString()
            }
        }

        return result
    }

    private fun factorial(n: Int): Long {
        if (n < 0) throw ArithmeticException("Factorial of negative number")
        if (n > 20) throw ArithmeticException("Factorial too large")
        if (n == 0 || n == 1) return 1
        var result = 1L
        for (i in 2..n) {
            result *= i
        }
        return result
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

        // Handle reciprocal (1/x)
        result = handleReciprocal(result)

        return result
    }

    private fun handleReciprocal(expr: String): String {
        var result = expr
        val pattern = "1/(\\([^)]+\\)|\\d+\\.?\\d*)".toRegex()

        while (pattern.containsMatchIn(result)) {
            result = pattern.replace(result) { matchResult ->
                val arg = matchResult.groupValues[1]
                val value = if (arg.startsWith("(")) {
                    evaluate(arg.substring(1, arg.length - 1))
                } else {
                    arg.toDouble()
                }
                if (value == 0.0) throw ArithmeticException("Division by zero")
                (1.0 / value).toString()
            }
        }

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
            value % 1.0 == 0.0 && abs(value) < 1e10 -> value.toLong().toString()
            else -> {
                val formatted = String.format("%.10f", value).trimEnd('0').trimEnd('.')
                // If number is very small or very large, use scientific notation
                if (abs(value) < 1e-6 || abs(value) > 1e10) {
                    String.format("%.6e", value)
                } else {
                    formatted
                }
            }
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
        val result = parseExpression()
        if (pos < expr.length) {
            throw IllegalArgumentException("Unexpected character at position $pos: ${expr[pos]}")
        }
        return result
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
        while (pos < expr.length && (expr[pos].isDigit() || expr[pos] == '.' || expr[pos] == 'e' || expr[pos] == 'E')) {
            pos++
            // Handle scientific notation
            if (pos < expr.length && (expr[pos - 1] == 'e' || expr[pos - 1] == 'E')) {
                if (pos < expr.length && (expr[pos] == '+' || expr[pos] == '-')) {
                    pos++
                }
            }
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