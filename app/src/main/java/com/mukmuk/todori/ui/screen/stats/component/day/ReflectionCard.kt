package com.mukmuk.todori.ui.screen.stats.component.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.ReflectionType
import com.mukmuk.todori.util.buildReflection
import com.mukmuk.todori.util.parseReflection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionCard(
    initialText: String = "",
    onReflectionChanged: (String) -> Unit = {},
    maxChars: Int = 30,
    modifier: Modifier = Modifier
) {
    var state by remember(initialText) { mutableStateOf(parseReflection(initialText)) }
    var editing: ReflectionType? by remember { mutableStateOf(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(Dimens.Nano)
    ) {
        Column(Modifier.fillMaxWidth().padding(Dimens.Medium)) {


            Row {
                Icon(Icons.Outlined.Edit, contentDescription = null)
                Spacer(Modifier.width(Dimens.Tiny))
                Text("오늘의 회고", style = AppTextStyle.BodyLarge)
            }
            Spacer(Modifier.height(Dimens.Small))


            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Nano)) {
                ReflectionChip(
                    label = "뭐가 잘 됐나?",
                    filled = state.good.isNotBlank(),
                    onClick = { editing = ReflectionType.GOOD }
                )
                ReflectionChip(
                    label = "내일 바꿀 점?",
                    filled = state.improve.isNotBlank(),
                    onClick = { editing = ReflectionType.IMPROVE }
                )
                ReflectionChip(
                    label = "방해 요인은?",
                    filled = state.blocker.isNotBlank(),
                    onClick = { editing = ReflectionType.BLOCKER }
                )
            }

            val preview = buildReflection(state)
            if (preview.isNotBlank()) {
                Spacer(Modifier.height(Dimens.Small))
                Text(preview, style = AppTextStyle.BodySmall.copy(color = Black))
            }
        }
    }


    if (editing != null) {
        val title = when (editing) {
            ReflectionType.GOOD -> "뭐가 잘 됐나?"
            ReflectionType.IMPROVE -> "내일 바꿀 점?"
            ReflectionType.BLOCKER -> "방해 요인은?"
            null -> ""
        }
        var text by remember(editing) {
            mutableStateOf(
                when (editing) {
                    ReflectionType.GOOD -> state.good
                    ReflectionType.IMPROVE -> state.improve
                    ReflectionType.BLOCKER -> state.blocker
                    else -> ""
                }
            )
        }

        ModalBottomSheet(
            onDismissRequest = { editing = null },
            containerColor = White,
            sheetState = sheetState
        ) {
            Column(Modifier.padding(Dimens.Large)) {
                Text(title, style = AppTextStyle.TitleSmall)
                Spacer(Modifier.height(Dimens.Medium))

                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= maxChars) text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${text.length} / $maxChars") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            state = when (editing) {
                                ReflectionType.GOOD -> state.copy(good = text.trim())
                                ReflectionType.IMPROVE -> state.copy(improve = text.trim())
                                ReflectionType.BLOCKER -> state.copy(blocker = text.trim())
                                else -> state
                            }
                            onReflectionChanged(buildReflection(state))
                            editing = null
                        }
                    )
                )

                Spacer(Modifier.height(Dimens.Medium))
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Tiny)) {
                    OutlinedButton(onClick = { editing = null }) { Text("취소") }
                    Button(onClick = {
                        state = when (editing) {
                            ReflectionType.GOOD -> state.copy(good = text.trim())
                            ReflectionType.IMPROVE -> state.copy(improve = text.trim())
                            ReflectionType.BLOCKER -> state.copy(blocker = text.trim())
                            else -> state
                        }
                        onReflectionChanged(buildReflection(state))
                        editing = null
                    }) { Text("저장") }
                }
                Spacer(Modifier.height(Dimens.Tiny))
            }
        }
    }
}
