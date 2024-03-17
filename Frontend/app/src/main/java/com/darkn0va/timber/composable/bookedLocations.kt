package com.darkn0va.timber.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.data.Booking
import com.darkn0va.timber.api.ktorHttpClient
import kotlinx.coroutines.runBlocking

@Composable
fun BookedLocations(
    userUUID: String,
    navController: NavController,
) {
    val api = LocationAPI(ktorHttpClient)
    var bookings by remember { mutableStateOf(listOf(Booking())) }
    var bookingsNull by remember { mutableStateOf(false) }
    var loadingBookings by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loadingBookings) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        } else if (bookingsNull) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                text = "You haven't booked any locations"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(bookings) { item ->
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
        }

        LaunchedEffect(userUUID) {
            val bookingResponse = api.getBookedLocations(userUUID)
            if (bookingResponse != null) {
                bookings = bookingResponse
                loadingBookings = false
            } else {
                bookingsNull = true
                loadingBookings = false
            }
        }
    }
}
