package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.MyStudy
import com.mukmuk.todori.ui.screen.community.StudyCategory
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagPickerBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    selectedTags: List<String>,
    onTagClick: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    if (show) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 120.dp),
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = White
        ) {
            LazyColumn(Modifier.padding(16.dp)) {
                item {
                    Text(
                        "태그 선택 (최대 3개)",
                        style = AppTextStyle.TitleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(StudyCategory.entries.toTypedArray()) { category ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(
                            text = category.displayName,
                            style = AppTextStyle.BodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            category.tags.forEach { tag ->
                                val isSelected = tag in selectedTags
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { if (!isSelected && selectedTags.size >= 3) {
                                        Toast.makeText(
                                            context,
                                            "태그는 최대 3개까지 선택 가능합니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        onTagClick(tag)
                                    }},
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
