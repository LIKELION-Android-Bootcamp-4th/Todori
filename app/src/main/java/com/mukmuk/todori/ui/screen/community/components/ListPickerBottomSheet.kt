package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPickerBottomSheet(
    show: Boolean,
    items: List<Study>,
    onDismissRequest: () -> Unit,
    onSelect: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = White
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("항목 선택", style = AppTextStyle.TitleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                items.forEach { item ->
                    CommunityItemData(
                        study = item,
                        memberCount = 0,
                    )
                }
            }
        }
    }
}