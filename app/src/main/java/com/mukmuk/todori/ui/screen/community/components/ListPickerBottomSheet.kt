package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.screen.todo.list.study.StudyListViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPickerBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (String) -> Unit
) {

    val studyListViewModel: StudyListViewModel = hiltViewModel()

    val studyListState by studyListViewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) {
        studyListViewModel.loadAllStudies("testuser")
    }

    val studies = studyListState.studies.values.toList()
    val membersMap = studyListState.membersMap

    if (show) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight()
            .padding(top = 120.dp),
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = White
        ) {
            LazyColumn(Modifier.padding(16.dp)) {
                item{
                Text("항목 선택", style = AppTextStyle.TitleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                }


                items(studies) { study ->
                    val memberCount = membersMap[study.studyId]?.size ?: 0
                    CommunityListData(
                        study = study,
                        memberCount = memberCount,
                        activeDays = study.activeDays,
                        onClick = {
                            onSelect(study.studyId)
                        }
                    )
                }
            }
        }
    }
}