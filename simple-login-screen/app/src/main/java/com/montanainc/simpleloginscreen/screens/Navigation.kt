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
//        composable(route = "Reset") {
//            PasswordResetScreen(navController)
//        }
//        composable(
//            route = "Equipment/{userId}/{email}",
//            arguments = listOf(
//                navArgument("userId") { type = NavType.IntType },
//                navArgument("email") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
//            val email = backStackEntry.arguments?.getString("email") ?: ""
//            EquipmentListScreen(
//                onBook = { item ->
//                    if (item.status == "AVAILABLE") {
//                        navController.navigate("Booking/$userId/${item.id}/$email")
//                    }
//                },
//                onMyRentals = { navController.navigate("MyRentals/$userId") },
//                onManager = { navController.navigate("Manager") }
//            )
//        }
//        composable(
//            route = "Booking/{userId}/{equipmentId}/{email}",
//            arguments = listOf(
//                navArgument("equipmentId") { type = NavType.IntType },
//                navArgument("userId") { type = NavType.IntType },
//                navArgument("email") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val equipmentId = backStackEntry.arguments?.getInt("equipmentId")
//            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
//            val email = backStackEntry.arguments?.getString("email") ?: ""
//            BookingScreen(
//                equipment = sampleEquipment.find { it.id == equipmentId },
//                onDone = { navController.popBackStack() },
//                userId = userId,
//                userEmail = email
//            )
//        }
//        composable(
//            route = "MyRentals/{userId}",
//            arguments = listOf(navArgument("userId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
//            MyRentalsScreen(
//                userId = userId,
//                onBack = { navController.popBackStack() }
//            )
//        }
//        composable(route = "Manager") {
//            ManagerScreen(onBack = { navController.popBackStack() })
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

// TESTING ONLY: true = skip login and open the equipment screen as a fake user.
// Set to false before committing and pushing.
const val BYPASS_AUTH = false
const val BYPASS_USER_ID = 0
const val BYPASS_EMAIL = "test@test.com"

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (BYPASS_AUTH) "Equipment/$BYPASS_USER_ID/$BYPASS_EMAIL" else "Login"
    ) {
        composable(route = "Login") {
            LoginScreen(navController)
        }
        composable(route = "Signup") {
            SignupScreen(navController)
        }
        composable(route = "Reset") {
            PasswordResetScreen(navController)
        }
        composable(
            route = "Equipment/{userId}/{email}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EquipmentListScreen(
                onBook = { item ->
                    if (item.status == "AVAILABLE") {
                        navController.navigate("Booking/$userId/${item.id}/$email")
                    }
                },
                onMyRentals = { navController.navigate("MyRentals/$userId") },
                onManager = { navController.navigate("Manager") },
                userId = userId,
                userEmail = email
            )
        }
        composable(
            route = "Booking/{userId}/{equipmentId}/{email}",
            arguments = listOf(
                navArgument("equipmentId") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val equipmentId = backStackEntry.arguments?.getInt("equipmentId")
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val email = backStackEntry.arguments?.getString("email") ?: ""
            BookingScreen(
                equipment = sampleEquipment.find { it.id == equipmentId },
                onDone = { navController.popBackStack() },
                userId = userId,
                userEmail = email
            )
        }
        composable(
            route = "MyRentals/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            MyRentalsScreen(
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = "Manager") {
            ManagerScreen(onBack = { navController.popBackStack() })
        }
    }
}