package com.maliknazeer.mydicegame.ui


sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object GameScreen : Screen("gameScreen_screen")
}