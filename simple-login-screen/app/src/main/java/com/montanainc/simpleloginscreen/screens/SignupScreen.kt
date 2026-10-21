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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.montanainc.simpleloginscreen.components.BottomComponent
import com.montanainc.simpleloginscreen.components.CheckboxComponent
import com.montanainc.simpleloginscreen.components.HeadingTextComponent
import com.montanainc.simpleloginscreen.components.MyTextFieldComponent
import com.montanainc.simpleloginscreen.components.NormalTextComponent
import com.montanainc.simpleloginscreen.components.PasswordTextFieldComponent

@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val signupUiState by viewModel.uiState.collectAsState()
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
            NormalTextComponent(value = "Hello there,")
            HeadingTextComponent(value = "Create an Account")
            Spacer(modifier = Modifier.height(25.dp))

            Column {
                MyTextFieldComponent(
                    labelValue = "First Name",
                    icon = Icons.Outlined.Person,
                    value = signupUiState.userDetails.firstName,
                    onValueChange = {newFirstName ->
                        viewModel.updateUiState(
                            signupUiState.userDetails.copy(firstName = newFirstName)
                        )
                    }

                )
                Spacer(modifier = Modifier.height(10.dp))
                MyTextFieldComponent(
                    labelValue = "Last Name",
                    icon = Icons.Outlined.Person,
                    value = signupUiState.userDetails.lastName,
                    onValueChange = { newLastName ->
                        viewModel.updateUiState(
                            signupUiState.userDetails.copy(lastName = newLastName)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
                MyTextFieldComponent(
                    labelValue = "Email",
                    icon = Icons.Outlined.Email,
                    value = signupUiState.userDetails.email,
                    onValueChange = { newEmail ->
                        viewModel.updateUiState(
                            signupUiState.userDetails.copy(email = newEmail)
                        )
                    }

                )
                Spacer(modifier = Modifier.height(10.dp))
                PasswordTextFieldComponent(
                    labelValue = "Password",
                    icon = Icons.Outlined.Lock,
                    value = signupUiState.userDetails.password,
                    onValueChange = { newPassword ->
                        viewModel.updateUiState(
                            signupUiState.userDetails.copy(password = newPassword)
                        )
                    }
                )
                CheckboxComponent()
                BottomComponent(
                    textQuery = "Already have an account? ",
                    textClickable = "Login",
                    action = "Register",
                    navController,
                    onButtonClicked = {
                        viewModel.registerUser {
                            navController.navigate("Login")
                        }
                    }
                )
            }
        }
    }
}