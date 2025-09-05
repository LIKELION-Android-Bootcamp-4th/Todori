package com.mukmuk.todori.ui.screen.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Daily
import com.mukmuk.todori.ui.theme.Danger
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.CardDefaultRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.Warning
import com.mukmuk.todori.ui.theme.White
import kotlin.math.roundToInt

@Composable
fun TargetSettingCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    currentMinutes: Int,
    maxMinutes: Int,
    stepMinutes: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    achievementRate: Float? = null
) {
    val currentHours = currentMinutes / 60f
    val maxHours = maxMinutes / 60f

    var textFieldValue by remember(currentHours) {
        mutableStateOf(if (currentHours % 1 == 0f) currentHours.toInt().toString() else String.format("%.1f", currentHours))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = CardDefaultRadius,
                ambientColor = backgroundColor.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.None),
        shape = CardDefaultRadius,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Large)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Medium))
                Text(
                    text = title,
                    style = AppTextStyle.AppBar,
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "목표 시간",
                    style = AppTextStyle.Body,
                    color = Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                            val hours = newValue.toFloatOrNull()
                            if (hours != null && hours >= 0) {
                                onValueChange((hours * 60).toInt())
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        modifier = Modifier.width(70.dp),
                        textStyle = AppTextStyle.BodyBold.copy(
                            textAlign = TextAlign.End
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconColor,
                            unfocusedBorderColor = DarkGray.copy(alpha = 0.3f),
                            cursorColor = iconColor
                        ),
                        shape = RoundedCornerShape(Dimens.Small)
                    )

                    Spacer(modifier = Modifier.width(Dimens.Tiny))

                    Text(
                        text = "시간",
                        style = AppTextStyle.BodyBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Large))

            CustomSlider(
                value = currentHours,
                onValueChange = { hours ->
                    val minutes = (hours * 60).roundToInt()
                    val steppedMinutes = (minutes / stepMinutes) * stepMinutes
                    onValueChange(steppedMinutes)
                },
                valueRange = 0f..maxHours,
                activeColor = iconColor,
                inactiveColor = iconColor.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.Nano),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "0시간",
                    style = AppTextStyle.BodyTinyMedium,
                    color = DarkGray
                )
                Text(
                    text = "${maxHours.toInt()}시간",
                    style = AppTextStyle.BodyTinyMedium,
                    color = DarkGray
                )
            }

            achievementRate?.let { rate ->
                Spacer(modifier = Modifier.height(Dimens.Large))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "예상 달성 확률",
                        style = AppTextStyle.BodySmallBold.copy(color = DarkGray)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Dimens.Tiny)
                                .background(
                                    color = when {
                                        rate >= 0.8f -> Daily
                                        rate >= 0.5f -> Warning
                                        else -> Danger
                                    },
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(Dimens.Nano))

                        Text(
                            text = "${(rate * 100).toInt()}%",
                            style = AppTextStyle.BodySmallBold,
                            color = when {
                                rate >= 0.8f -> Daily
                                rate >= 0.5f -> Warning
                                else -> Danger
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Small))

                CustomLinearProgressBar(
                    progress = rate,
                    modifier = Modifier.fillMaxWidth(),
                    height = Dimens.Nano,
                    progressColor = when {
                        rate >= 0.8f -> Daily
                        rate >= 0.5f -> Warning
                        else -> Danger
                    },
                    trackColor = Gray,
                    cornerRadius = Dimens.Nano
                )
            }
        }
    }
}

