package com.example.internassessmentapp.ui.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internassessmentapp.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onBack: () -> Unit,
    viewModel: CalculatorViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scientific Calculator") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Expression Display
                    Text(
                        text = viewModel.expression.value,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    // Result Display
                    Text(
                        text = viewModel.result.value,
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 36.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scientific Functions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScientificButton("sin", Modifier.weight(1f)) { viewModel.addFunction("sin(") }
                ScientificButton("cos", Modifier.weight(1f)) { viewModel.addFunction("cos(") }
                ScientificButton("tan", Modifier.weight(1f)) { viewModel.addFunction("tan(") }
                ScientificButton("√", Modifier.weight(1f)) { viewModel.addFunction("√(") }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScientificButton("log", Modifier.weight(1f)) { viewModel.addFunction("log(") }
                ScientificButton("ln", Modifier.weight(1f)) { viewModel.addFunction("ln(") }
                ScientificButton("x²", Modifier.weight(1f)) { viewModel.addOperator("^2") }
                ScientificButton("xʸ", Modifier.weight(1f)) { viewModel.addOperator("^") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Number Pad and Operations
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton("C", Modifier.weight(1f), true) { viewModel.clear() }
                    CalculatorButton("(", Modifier.weight(1f)) { viewModel.addOperator("(") }
                    CalculatorButton(")", Modifier.weight(1f)) { viewModel.addOperator(")") }
                    OperatorButton("÷", Modifier.weight(1f)) { viewModel.addOperator("/") }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton("7", Modifier.weight(1f)) { viewModel.addNumber("7") }
                    CalculatorButton("8", Modifier.weight(1f)) { viewModel.addNumber("8") }
                    CalculatorButton("9", Modifier.weight(1f)) { viewModel.addNumber("9") }
                    OperatorButton("×", Modifier.weight(1f)) { viewModel.addOperator("*") }
                }

                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton("4", Modifier.weight(1f)) { viewModel.addNumber("4") }
                    CalculatorButton("5", Modifier.weight(1f)) { viewModel.addNumber("5") }
                    CalculatorButton("6", Modifier.weight(1f)) { viewModel.addNumber("6") }
                    OperatorButton("−", Modifier.weight(1f)) { viewModel.addOperator("-") }
                }

                // Row 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton("1", Modifier.weight(1f)) { viewModel.addNumber("1") }
                    CalculatorButton("2", Modifier.weight(1f)) { viewModel.addNumber("2") }
                    CalculatorButton("3", Modifier.weight(1f)) { viewModel.addNumber("3") }
                    OperatorButton("+", Modifier.weight(1f)) { viewModel.addOperator("+") }
                }

                // Row 5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton("±", Modifier.weight(1f)) { viewModel.toggleSign() }
                    CalculatorButton("0", Modifier.weight(1f)) { viewModel.addNumber("0") }
                    CalculatorButton(".", Modifier.weight(1f)) { viewModel.addDecimal() }
                    EqualsButton("=", Modifier.weight(1f)) { viewModel.calculate() }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Back to Menu")
            }
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