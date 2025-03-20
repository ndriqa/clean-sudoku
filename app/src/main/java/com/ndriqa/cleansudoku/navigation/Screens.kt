package com.ndriqa.cleansudoku.navigation

sealed class Screens(val route: String) {

    object Home : Screens(route = "home")
    object Sudoku : Screens(route = "sudoku")
    object History : Screens(route = "history")
    object ScreenTwo : Screens(route = "screenTwo/{parameter}") {
        fun createRoute(parameter: String) = "screenTwo/$parameter"
    }
}