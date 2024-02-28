package com.darkn0va.timber.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.ktorHttpClient
import kotlinx.coroutines.runBlocking

@Composable
fun BookedLocations(
    userUUID: String,
    navController: NavController,
) {
    val api = LocationAPI(ktorHttpClient)
    var bookings by remember { mutableStateOf(listOf(com.darkn0va.timber.api.data.Booking())) }
    Column {
        Text(bookings.toString())
    }

    runBlocking {
        bookings = api.getBookedLocations(userUUID)
    }
}