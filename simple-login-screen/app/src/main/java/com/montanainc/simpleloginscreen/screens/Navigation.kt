//package com.montanainc.simpleloginscreen.screens
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//
//@Composable
//fun Navigation() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "Login"
//    ) {
//        composable(route = "Login") {
//            LoginScreen(navController)
//        }
//        composable(route = "Signup") {
//            SignupScreen(navController)
//        }
//        composable(route = "Equipment") {
//            EquipmentListScreen(
//                onBook = { item -> navController.navigate("Booking/${item.id}") },
//                onMyRentals = { navController.navigate("MyRentals") },
//                onManager = { navController.navigate("Manager") }
//            )
//        }
//        composable(
//            route = "Booking/{equipmentId}",
//            arguments = listOf(navArgument("equipmentId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val id = backStackEntry.arguments?.getInt("equipmentId")
//            BookingScreen(
//                equipment = sampleEquipment.find { it.id == id },
//                onDone = { navController.popBackStack() }
//            )
//        }
//        composable(route = "MyRentals") {
//            MyRentalsScreen(onBack = { navController.popBackStack() })
//        }
//    }
//}

package com.montanainc.simpleloginscreen.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// TESTING ONLY: true = skip login and start on the equipment screen.
// Set to false before pushing so the real login flow is used.
const val BYPASS_AUTH = true

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (BYPASS_AUTH) "Equipment" else "Login"
    ) {
        composable(route = "Login") {
            LoginScreen(navController)
        }
        composable(route = "Signup") {
            SignupScreen(navController)
        }
        composable(route = "Equipment") {
            EquipmentListScreen(
                onBook = { item -> navController.navigate("Booking/${item.id}") },
                onMyRentals = { navController.navigate("MyRentals") },
                onManager = { navController.navigate("Manager") }
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
        composable(route = "Manager") {
            ManagerScreen(onBack = { navController.popBackStack() })
        }
    }
}