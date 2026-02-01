package com.example.internassessmentapp.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internassessmentapp.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onBack: () -> Unit,
    viewModel: CalculatorViewModel = viewModel()
) {
    var showHistory by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new calculation is performed
    LaunchedEffect(viewModel.history.value.size) {
        if (viewModel.history.value.isNotEmpty()) {
            coroutineScope.launch {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scientific Calculator") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showHistory = !showHistory }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = if (showHistory)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (showHistory) {
            HistoryView(
                history = viewModel.history.value,
                onClearHistory = { viewModel.clearHistory() },
                onSelectHistory = { historyItem ->
                    viewModel.expression.value = historyItem.expression
                    viewModel.result.value = historyItem.result
                    viewModel.cursorPosition.value = historyItem.expression.length
                    showHistory = false
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display Area with scrollable history
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Show calculation history - each on one line
                        viewModel.history.value.take(5).reversed().forEach { historyItem ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        viewModel.loadFromHistory(historyItem.result)
                                    }
                            ) {
                                // Single line for expression and result
                                Text(
                                    text = "${historyItem.expression} = ${historyItem.result}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )
                            }
                        }

                        if (viewModel.history.value.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Current Expression with cursor - Touch to position
                        EditableExpressionDisplay(
                            expression = viewModel.expression.value,
                            cursorPosition = viewModel.cursorPosition.value,
                            onCursorPositionChange = { viewModel.setCursorPosition(it) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Result Display
                        Text(
                            text = viewModel.result.value,
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scientific Functions Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificButton("sin", Modifier.weight(1f)) { viewModel.addFunction("sin(") }
                    ScientificButton("cos", Modifier.weight(1f)) { viewModel.addFunction("cos(") }
                    ScientificButton("tan", Modifier.weight(1f)) { viewModel.addFunction("tan(") }
                    ScientificButton("√", Modifier.weight(1f)) { viewModel.addSmartSquareRoot() }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scientific Functions Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificButton("log", Modifier.weight(1f)) { viewModel.addFunction("log(") }
                    ScientificButton("ln", Modifier.weight(1f)) { viewModel.addFunction("ln(") }
                    ScientificButton("x²", Modifier.weight(1f)) { viewModel.addSquare() }
                    ScientificButton("xʸ", Modifier.weight(1f)) { viewModel.addOperator("^") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scientific Functions Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificButton("%", Modifier.weight(1f)) { viewModel.addOperator("%") }
                    ScientificButton("π", Modifier.weight(1f)) { viewModel.addFunction("π") }
                    ScientificButton("x!", Modifier.weight(1f)) { viewModel.addOperator("!") }
                    ScientificButton("1/x", Modifier.weight(1f)) { viewModel.addReciprocal() }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Number Pad and Operations
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1 - AC and Backspace (⌫) with same color
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("AC", Modifier.weight(1f), true) { viewModel.clear() }
                        CalculatorButton("⌫", Modifier.weight(1f), true) { viewModel.backspace() }
                        CalculatorButton("(", Modifier.weight(1f)) { viewModel.addOperator("(") }
                        OperatorButton("+", Modifier.weight(1f)) { viewModel.addOperator("+") }
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("7", Modifier.weight(1f)) { viewModel.addNumber("7") }
                        CalculatorButton("8", Modifier.weight(1f)) { viewModel.addNumber("8") }
                        CalculatorButton("9", Modifier.weight(1f)) { viewModel.addNumber("9") }
                        OperatorButton("÷", Modifier.weight(1f)) { viewModel.addOperator("/") }
                    }

                    // Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("4", Modifier.weight(1f)) { viewModel.addNumber("4") }
                        CalculatorButton("5", Modifier.weight(1f)) { viewModel.addNumber("5") }
                        CalculatorButton("6", Modifier.weight(1f)) { viewModel.addNumber("6") }
                        OperatorButton("×", Modifier.weight(1f)) { viewModel.addOperator("*") }
                    }

                    // Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("1", Modifier.weight(1f)) { viewModel.addNumber("1") }
                        CalculatorButton("2", Modifier.weight(1f)) { viewModel.addNumber("2") }
                        CalculatorButton("3", Modifier.weight(1f)) { viewModel.addNumber("3") }
                        OperatorButton("−", Modifier.weight(1f)) { viewModel.addOperator("-") }
                    }

                    // Row 5
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton("0", Modifier.weight(1f)) { viewModel.addNumber("0") }
                        CalculatorButton(".", Modifier.weight(1f)) { viewModel.addDecimal() }
                        CalculatorButton(")", Modifier.weight(1f)) { viewModel.addOperator(")") }
                        EqualsButton("=", Modifier.weight(1f)) { viewModel.calculate() }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableExpressionDisplay(
    expression: String,
    cursorPosition: Int,
    onCursorPositionChange: (Int) -> Unit
) {
    var textFieldValue by remember(expression, cursorPosition) {
        mutableStateOf(
            TextFieldValue(
                text = expression,
                selection = TextRange(cursorPosition)
            )
        )
    }

    // Update textFieldValue when expression or cursor changes externally
    LaunchedEffect(expression, cursorPosition) {
        if (textFieldValue.text != expression || textFieldValue.selection.start != cursorPosition) {
            textFieldValue = TextFieldValue(
                text = expression,
                selection = TextRange(cursorPosition)
            )
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onCursorPositionChange(newValue.selection.start)
        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        singleLine = false,
        maxLines = 3,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                innerTextField()
            }
        }
    )
}

@Composable
fun HistoryView(
    history: List<com.example.internassessmentapp.viewmodel.CalculationHistory>,
    onClearHistory: () -> Unit,
    onSelectHistory: (com.example.internassessmentapp.viewmodel.CalculationHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calculation History",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (history.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Text("Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HistoryToggleOff,
                        contentDescription = "No history",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No calculation history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { historyItem ->
                    HistoryItem(
                        historyItem = historyItem,
                        onClick = { onSelectHistory(historyItem) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    historyItem: com.example.internassessmentapp.viewmodel.CalculationHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${historyItem.expression} = ${historyItem.result}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3
            )
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSpecial)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surface,
            contentColor = if (isSpecial)
                MaterialTheme.colorScheme.onErrorContainer
            else
                MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 20.sp)
    }
}

@Composable
fun OperatorButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 24.sp)
    }
}

@Composable
fun ScientificButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun EqualsButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 28.sp)
    }
}