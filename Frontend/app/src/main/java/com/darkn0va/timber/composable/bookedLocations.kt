package com.darkn0va.timber.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.data.Booking
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.ktorHttpClient
import com.darkn0va.timber.`fun`.capitalized

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
                modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookings) { item ->
                    var location by remember { mutableStateOf(Location()) }
                    LaunchedEffect(item) {
                        location = api.getLocation(item.locationUUID)
                    }
                    Column(
                        modifier = Modifier.clip(
                            RoundedCornerShape(16.dp)
                        ).background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp).clickable {
                            navController.navigate("location/${item.locationUUID}")
                        }
                    ) {
                        Text(
                            text = location.name.capitalized(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.displaySmall
                        )

                        Spacer(modifier = Modifier.padding(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(text = item.bookedFrom.toString())
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(text = item.bookedTill.toString())
                                    }
                                }
                            }
                        }
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
