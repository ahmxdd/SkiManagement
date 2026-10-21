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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.montanainc.simpleloginscreen.components.BottomComponent
import com.montanainc.simpleloginscreen.components.HeadingTextComponent
import com.montanainc.simpleloginscreen.components.MyTextFieldComponent
import com.montanainc.simpleloginscreen.components.NormalTextComponent
import com.montanainc.simpleloginscreen.components.PasswordTextFieldComponent

@Composable
fun LoginScreen(
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
                NormalTextComponent(value = "Hey, there")
                HeadingTextComponent(value = "Welcome Back")
            }
            Spacer(modifier = Modifier.height(25.dp))
            Column {
                MyTextFieldComponent(labelValue = "Email", icon = Icons.Outlined.Email,
                value = loginUiState.userDetails.email, onValueChange = {newEmail ->
                    viewModel.updateUiState(
                        newDetails =  loginUiState.userDetails.copy(email = newEmail)
                    )
                    })
                Spacer(modifier = Modifier.height(10.dp))
                PasswordTextFieldComponent(
                    labelValue = "Password",
                    icon = Icons.Outlined.Lock,
                    value = loginUiState.userDetails.password,
                    onValueChange = { newPassword ->
                        viewModel.updateUiState(
                            newDetails = loginUiState.userDetails.copy(password = newPassword)
                        )
                    }
                )
            }
            BottomComponent(
                textQuery = "Don't have an account? ",
                textClickable = "Register",
                action = "Login",
                navController,
                onButtonClicked = {
                    viewModel.loginUser {
                        navController.navigate("Signup")
                    }
                }
            )
        }
    }
}