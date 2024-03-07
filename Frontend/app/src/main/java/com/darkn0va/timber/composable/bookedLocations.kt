package com.darkn0va.timber.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    LazyColumn {
        items(bookings) {item ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.bookedFrom.toString())
                Text(text = item.bookedTill.toString())
            }
        }
    }

    runBlocking {
        bookings = api.getBookedLocations(userUUID)
    }
}