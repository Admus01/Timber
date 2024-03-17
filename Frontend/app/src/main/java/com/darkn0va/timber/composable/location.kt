package com.darkn0va.timber.composable

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.darkn0va.timber.api.ImageAPI
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.api.ktorHttpClient
import com.darkn0va.timber.api.supabase
import com.darkn0va.timber.`fun`.capitalized

@Composable
fun LocationCom(
    navController: NavController,
    locationUUID: String,
    user: User
) {
    var image: Bitmap? by remember { mutableStateOf(null) }
    val api = LocationAPI(ktorHttpClient)
    val client = ImageAPI(supabase)
    var loadingLocations by remember { mutableStateOf(true) }
    var location by remember { mutableStateOf(Location()) }
    var loadingImage by remember { mutableStateOf(true) }

    LaunchedEffect(locationUUID) {
        location = api.getLocation(locationUUID)
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
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


        }
    }
}