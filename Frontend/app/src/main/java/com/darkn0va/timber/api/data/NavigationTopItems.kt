package com.darkn0va.timber.api.data

data class NavigatioTopnBarItem(
    val title: String,
    val route: String
)

val NavigationTopItems = listOf(
    NavigatioTopnBarItem(
        title = "My locations",
        route = "userLocations"
    ),
    NavigatioTopnBarItem(
        title = "Booked locations",
        route = "bookedLocations"
    )
)