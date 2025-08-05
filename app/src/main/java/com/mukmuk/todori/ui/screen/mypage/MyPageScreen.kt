package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.ui.mypage.component.ProfileSection
import com.mukmuk.todori.ui.screen.mypage.component.MyPageAccountSection
import com.mukmuk.todori.ui.screen.mypage.component.MyPageMenuSection
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun MyPageScreen(navController: NavController) {
    var currentUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { user ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document("testuser")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        currentUser = document.toObject(User::class.java)
                    }
                }
        }
    }
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(Dimens.Medium)
        ) {
            currentUser?.let {
                ProfileSection(user = it)
            }
            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            MyPageMenuSection(
                onLevelClick = {navController.navigate("myLevel")},
                onGoalClick = {navController.navigate("completedGoals")},
                onProfileEditClick = {navController.navigate("profileManage")}
            )

            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            MyPageAccountSection(
                onLogoutClick = {
                    navController.navigate("login")
                },
                onDeleteAccountClick = {}
            )
        }
    }
}
