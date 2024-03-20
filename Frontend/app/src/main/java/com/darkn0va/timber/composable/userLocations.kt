package com.darkn0va.timber.composable

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.darkn0va.timber.api.ImageAPI
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.ktorHttpClient
import com.darkn0va.timber.api.supabase
import com.darkn0va.timber.`fun`.capitalized
import com.darkn0va.timber.ui.theme.GreyBG

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UserLocations(
    userUUID: String,
    navController: NavController,
) {
    val api = LocationAPI(ktorHttpClient)
    val client = ImageAPI(supabase)
    var loadingLocations by remember { mutableStateOf(true) }
    var locations by remember { mutableStateOf(listOf(Location())) }
    var locationsNull by remember { mutableStateOf(false) }
    LaunchedEffect(userUUID) {
        val locationResponse = api.getUserLocations(userUUID)
        if (locationResponse != null) {
            locations = locationResponse
            loadingLocations = false
        } else {
            locationsNull = true
            loadingLocations = false
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loadingLocations) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        } else if (locationsNull) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                text = "You haven't created any locations"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(locations) { _, item ->
                    var image: Bitmap? by remember { mutableStateOf(null) }
                    var loadingImage by remember { mutableStateOf(true) }
                    LaunchedEffect(item.image1) {
                        try {
                            image = client.getImage(item.image1)
                            loadingImage = false
                        } catch (_: Throwable) {

                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(2.dp).background(GreyBG).padding(4.dp)
                            .clickable(onClick = {
                                navController.navigate("location/${item.locationUUID}")
                            }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (loadingImage) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(64.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            } else {
                                image?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.name)
                            Spacer(modifier = Modifier.weight(1f))
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (item.addressState.isNullOrBlank()) {
                                    Text(text = item.addressCountry.capitalized())
                                    Text(text = item.addressCity.capitalized())
                                } else {
                                    Text(text = "${item.addressCountry.capitalized()}, ${item.addressState}")
                                    Text(text = item.addressCity.capitalized())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}