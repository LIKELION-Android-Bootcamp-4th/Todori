package com.mukmuk.todori.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.LightGray

@Composable
fun TimerTextFieldInput(
    initialMinutes: Int = 0,
    initialSeconds: Int = 0,
    onTimeChanged: (Int, Int) -> Unit
) {
    var minutesText by remember { mutableStateOf(initialMinutes.toString().padStart(2, '0')) }
    var secondsText by remember { mutableStateOf(initialSeconds.toString().padStart(2, '0')) }

    fun sanitizeAndClampInput(text: String, max: Int): String {
        val digitsOnly = text.filter { it.isDigit() }
        val number = digitsOnly.toIntOrNull()?.coerceIn(0, max) ?: 0
        return number.toString().padStart(2, '0')
    }

    Row(
        modifier = Modifier
            .background(color = LightGray, shape = RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TimerUnitTextField(
            value = minutesText,
            onValueChange = {
                minutesText = it
                onTimeChanged(it.toInt(), secondsText.toInt())
            }
        )
        Text(
            text = ":",
            style = AppTextStyle.TitleMedium.copy(
                textAlign = TextAlign.Center
            ),
        )
        TimerUnitTextField(
            value = secondsText,
            onValueChange = {
                secondsText = it
                onTimeChanged(minutesText.toInt(), it.toInt())
            }
        )
    }
}

@Composable
fun TimerUnitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = { input ->
            val sanitized = input.filter { it.isDigit() }.toIntOrNull()?.coerceIn(0, 59)
                ?.toString()?.padStart(2, '0') ?: "00"
            onValueChange(sanitized)
        },
        modifier = modifier
            .width(60.dp)
            .height(60.dp),
        textStyle = AppTextStyle.TitleMedium.copy(
            textAlign = TextAlign.Center
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        )
    )
}