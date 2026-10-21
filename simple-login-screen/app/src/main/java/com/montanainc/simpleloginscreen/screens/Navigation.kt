package com.montanainc.simpleloginscreen.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Login"
    ) {
        composable(route = "Login") {
            LoginScreen(navController)
        }
        composable(route = "Signup") {
            SignupScreen(navController)
        }
        composable(route = "Equipment") {
            EquipmentListScreen(
                equipment = sampleEquipment,
                onBook = { item -> navController.navigate("Booking/${item.id}") },
                onMyRentals = { navController.navigate("MyRentals") }
            )
        }
        composable(
            route = "Booking/{equipmentId}",
            arguments = listOf(navArgument("equipmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("equipmentId")
            BookingScreen(
                equipment = sampleEquipment.find { it.id == id },
                onDone = { navController.popBackStack() }
            )
        }
        composable(route = "MyRentals") {
            MyRentalsScreen(onBack = { navController.popBackStack() })
        }
    }
}