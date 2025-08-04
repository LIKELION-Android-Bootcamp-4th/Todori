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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
    // TextFieldValue를 사용해 커서 위치 제어
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    // 외부에서 value가 업데이트되면 내부 상태도 갱신
    if (textFieldValue.text != value) {
        textFieldValue = TextFieldValue(
            text = value,
            selection = TextRange(value.length)
        )
    }

    TextField(
        value = textFieldValue,
        onValueChange = { input ->
            val rawInput = input.text
            val sanitized = rawInput.filter { it.isDigit() }
                .toIntOrNull()
                ?.coerceIn(0, 59)
                ?.toString()
                ?.padStart(2, '0') ?: "00"

            // 상태 업데이트: 커서는 항상 맨 뒤로
            textFieldValue = TextFieldValue(
                text = sanitized,
                selection = TextRange(sanitized.length)
            )

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
