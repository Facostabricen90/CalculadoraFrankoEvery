package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    val expression by remember { mutableStateOf(StringBuilder()) }
    var previousResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            color = Color.White,
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "x"),
            listOf("1", "2", "3", "-"),
            listOf("0", "C", "=", "+")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { buttonText ->
                    CalculatorButton(buttonText) {
                        when (buttonText) {
                            "C" -> {
                                display = "0"
                                expression.clear()
                                previousResult = null
                            }
                            "=" -> {
                                val currentExpression = expression.toString()
                                val result = evaluateExpression(currentExpression, previousResult)

                                display = result
                                previousResult = if (result != "Error") result else null
                                expression.clear()
                            }
                            else -> {
                                if (display == "0") {
                                    display = buttonText
                                } else {
                                    display += buttonText
                                }
                                expression.append(buttonText.replace("x", "*")) // Convertir 'x' a '*'
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .background(Color.Black, shape = RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
    ) {
        Text(text = text, color = Color.White, fontSize = 24.sp)
    }
}

fun evaluateExpression(expression: String, previousResult: String? = null): String {
    val fullExpression = if (previousResult != null) {
        "$previousResult$expression"
    } else {
        expression
    }
    return try {
        val tokens = fullExpression.split("([+\\-*/])".toRegex())
        if (tokens.size != 2) return "Error"

        val num1 = tokens[0].toDoubleOrNull()
        val num2 = tokens[1].toDoubleOrNull()

        val operator = fullExpression.find { it == '+' || it == '-' || it == '*' || it == '/' }

        if (num1 == null || num2 == null || operator == null || num1 !in -10000.0..10000.0 || num2 !in -10000.0..10000.0) {
            return "Error"
        }

        val result: Double = when (operator) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            '*' -> num1 * num2
            '/' -> if (num2 != 0.0) {
                num1 / num2
            } else {
                return "Error"
            }
            else -> return "Error"
        }

        roundToTwoDecimals(result)
    } catch (e: Exception) {
        "Error"
    }
}

private fun roundToTwoDecimals(value: Double): String {
    return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString()
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MyApplicationTheme {
        CalculatorScreen()
    }
}
