
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.component.TodoItemEditableRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyTodoInputCard(
    taskList: List<StudyTodo>,
    newTodoText: String,
    onTodoTextChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onToggleChecked: (String, Boolean) -> Unit,
    onDelete: (String) -> Unit,
    progressMap: Map<String, TodoProgress>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Small),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, DarkGray)
    ) {
        Column(modifier = Modifier.padding(Dimens.Small)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.CheckCircleOutline, tint = GroupPrimary, contentDescription = null)
                Spacer(modifier = Modifier.width(Dimens.Small))
                Text("오늘의 Todo", style = AppTextStyle.Body)

            }
            Spacer(modifier = Modifier.height(Dimens.Small))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newTodoText,
                    onValueChange = onTodoTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("할 일을 입력하세요") }
                )
                Spacer(modifier = Modifier.width(Dimens.Small))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = GroupPrimary,shape = RoundedCornerShape(DefaultCornerRadius))
                ) {
                    IconButton(
                        onClick = {
                            onAddClick()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "추가")
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            taskList.forEachIndexed { i, todo ->
                val isDone = progressMap[todo.studyTodoId]?.done == true

                TodoItemEditableRow(
                    title = todo.title,
                    isDone = isDone,
                    modifier = Modifier.padding(Dimens.Nano),
                    onCheckedChange = { checked ->
                        onToggleChecked(todo.studyTodoId, checked)
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = "삭제",
                            tint = Red,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onDelete(todo.studyTodoId) }
                        )
                    }
                )
            }
        }
    }
}
