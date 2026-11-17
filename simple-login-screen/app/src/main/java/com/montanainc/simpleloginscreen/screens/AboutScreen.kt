package com.montanainc.simpleloginscreen.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.montanainc.simpleloginscreen.components.BottomComponent
import com.montanainc.simpleloginscreen.components.HeadingTextComponent
import com.montanainc.simpleloginscreen.components.MyTextFieldComponent
import com.montanainc.simpleloginscreen.components.NormalTextComponent
import com.montanainc.simpleloginscreen.components.PasswordTextFieldComponent
import com.montanainc.simpleloginscreen.viewmodel.AppViewModelProvider
import com.montanainc.simpleloginscreen.viewmodel.UserViewModel

@Composable
fun AboutScreen (
    navController: NavHostController,
    viewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val loginUiState by viewModel.uiState.collectAsState()
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                NormalTextComponent(
                    value = "About Us\n\n" +
                        "We're a local ski rental shop offering quality skis, " +
                        "snowboards, boots, and helmets for every skill level.\n\n" +
                        "Browse our equipment, book online, and pick up your gear " +
                        "ready to hit the slopes."
                )
                Spacer(modifier = Modifier.height(25.dp))
                HeadingTextComponent(value = "HOURS")
                NormalTextComponent(
                    value = "Sunday: CLOSED\n\n" +
                            "Monday: 9-5 pm \n\n" +
                            "Tuesday: 9-5 pm \n\n" +
                            "Wednesday: 9-5 pm \n\n" +
                            "Thursday: 9-5 pm \n\n" +
                            "Friday: 9-5 pm \n\n" +
                            "Saturday: 9-5 pm \n\n"
                )
            }
        }
    }
}