package com.mukmuk.todori.ui.screen.community

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySearchScreen(

) {

    Scaffold(

        topBar = {
            TopAppBar(
                title = {Text("")},

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },

                actions = {
                    IconButton (
                        onClick = {

                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "이전")
                    }
                }
            )
        },


    ) {

    }

}