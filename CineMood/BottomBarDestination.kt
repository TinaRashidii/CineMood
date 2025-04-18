package com.example.cinemood

import com.example.cinemood.destinations.DirectionDestination
import com.example.cinemood.destinations.FavoritesScreenDestination
import com.example.cinemood.destinations.HomeScreenDestination
import com.example.cinemood.destinations.MoodToMovieScreenDestination

enum class BottomBarDestination(
    val label: String,
    val direction: DirectionDestination?
) {
    Home("خانه", HomeScreenDestination),
    Favorites("دوست داشتنی ها", FavoritesScreenDestination),
    Mood("مود", MoodToMovieScreenDestination),
    ThemeToggle("تم", null)
}
