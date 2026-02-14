package com.maliknazeer.mydicegame

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maliknazeer.mydicegame.ui.Screen
import com.maliknazeer.mydicegame.ui.screens.SharedViewModel
import com.maliknazeer.mydicegame.ui.screens.gamescreen.GameScreen
import com.maliknazeer.mydicegame.ui.screens.homescreen.HomeScreen

@Composable
fun Navigation(){
    val sharedViewModel = SharedViewModel()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ){
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController, sharedViewModel)
        }
        composable(route = Screen.GameScreen.route) {
            GameScreen(navController, sharedViewModel)
        }
    }
}