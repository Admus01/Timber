package com.darkn0va.timber.composable

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.darkn0va.timber.api.*
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.api.data.UserUUID
import com.darkn0va.timber.`fun`.capitalized

@Composable
fun LocationCom(
    navController: NavController,
    locationUUID: String,
    userCurrent: User
) {
    var image: Bitmap? by remember { mutableStateOf(null) }
    val apiLocation = LocationAPI(ktorHttpClient)
    val apiUser = UserAPI(ktorHttpClient)
    val client = ImageAPI(supabase)
    var loadingLocations by remember { mutableStateOf(true) }
    var location by remember { mutableStateOf(Location()) }
    var user by remember { mutableStateOf(User()) }
    var loadingImage by remember { mutableStateOf(true) }

    LaunchedEffect(locationUUID) {
        location = apiLocation.getLocation(locationUUID)
//        location = Location(
//            locationUUID = "3a9f6251-a476-488f-ad35-027eb66c3e91",
//            userUUID = "d36719e8-f5f1-408c-b81b-5d2d1132be8a",
//            name = "moderní byt v centru",
//            beds = 4,
//            description = "elegantní byt s veškerým komfortem, v blízkosti restaurací a památek.",
//            addressCity = "historické město",
//            addressStreet = "náměstí svobody 8",
//            addressState = null,
//            addressApartmentNumber = null,
//            addressCountry = "kulturní království",
//            image1 = "https://example.com/image4.jpg",
//            image2 = null,
//            image3 = null,
//            image4 = null,
//            image5 = null,
//            image6 = null,
//            image7 = null,
//            image8 = null,
//            image9 = null,
//            image10 = null,
//            rating = 0.0f,
//            isActive = true
//        )
        user = apiUser.getUserData(UserUUID(location.userUUID.toString()))
        loadingLocations = false
    }

    LaunchedEffect(location.image1) {
        try {
            image = client.getImage(location.image1)
            loadingImage = false
        } catch (e: Throwable) {
            Log.d("SHTF", e.toString())
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loadingLocations) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            Text(
                text = location.name.capitalized(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.padding(8.dp))
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
            Spacer(modifier = Modifier.padding(8.dp))

//            Text(
//                "Address Information",
//                Modifier.align(Alignment.Start),
//                fontSize = 18.sp,
//                color = MaterialTheme.colorScheme.onSurface
//            )
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(" Address", color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.weight(1f))
                Column {
                    Text(
                        "${location.addressCity.capitalized()}, ${location.addressStreet.capitalized()}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(location.addressCountry.capitalized(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.padding(2.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(" Beds", color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.weight(1f))
                Column(
                ) {
                    Text(location.beds.toString(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                VerticalDivider(color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.fillMaxHeight())
                Spacer(modifier = Modifier.weight(1f))
                Text(" Rating", color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.weight(1f))
                Column(
                ) {
                    Text(location.rating.toString(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        Spacer(Modifier.height(2.dp))
        Text(
            " Description",
            Modifier.align(Alignment.Start),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.padding(2.dp).fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer)
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(location.description.capitalized(), color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Row(
            modifier = Modifier.padding(2.dp).fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).clip(RoundedCornerShape(16.dp))
                .padding(4.dp).clickable(onClick = {
                    navController.navigate("createBooking/${location.locationUUID.toString()}")
                }),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.BorderColor, "create booking", Modifier.size(32.dp))
            Text(" Create booking", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, "chevron right", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}