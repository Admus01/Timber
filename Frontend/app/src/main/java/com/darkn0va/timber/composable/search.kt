package com.darkn0va.timber.composable

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.darkn0va.timber.R
import com.darkn0va.timber.api.ImageAPI
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.ktorHttpClient
import com.darkn0va.timber.api.supabase
import com.darkn0va.timber.`fun`.capitalized
import com.darkn0va.timber.ui.theme.GreyBG

@Composable
fun Search(navController: NavController) {
    val api = LocationAPI(ktorHttpClient)
    val client = ImageAPI(supabase)
    val context = LocalContext.current
    var loadingLocations by remember { mutableStateOf(false) }
    var validated by rememberSaveable { mutableStateOf(false) }
    var locations by remember { mutableStateOf(listOf(Location())) }
    var locationsNull by remember { mutableStateOf(true) }
    var addressInformationValidated by remember { mutableStateOf("") }
    var addressInformation by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(validated) {
        if (validated) {
            val locationResponse = api.locationSearch(1, addressInformationValidated.lowercase())
            if (locationResponse != null) {
                locationsNull = false
                locations = locationResponse
                loadingLocations = false
            } else {
                locationsNull = true
                loadingLocations = false
            }
        }
        validated = false
    }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.locationSearch) + "*",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
            value = addressInformation,
            onValueChange = { addressInformation = it },
            placeholder = { Text(text = "e.g. Karviná") },
        )


        Spacer(modifier = Modifier.padding(8.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                validateSearch(
                    addressInformation = addressInformation,
                    onInvalidate = { resourceId ->
                        val formattedString =
                            context.getString(R.string.value_is_empty) + " " + context.getString(resourceId)
                        Toast.makeText(
                            context,
                            formattedString,
                            Toast.LENGTH_LONG
                        ).show()
                        validated = false
                    },
                    onValidate = {
                        addressInformationValidated = addressInformation
                        Toast.makeText(
                            context,
                            context.getString(R.string.searchSucces),
                            Toast.LENGTH_LONG
                        ).show()
                        loadingLocations = true
                        validated = true
                    }
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text(
                text = "Search",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        if (loadingLocations) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        } else if (locationsNull) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                text = "Search doesnt match any locations"
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

private fun validateSearch(
    addressInformation: String,
    onInvalidate: (Int) -> Unit,
    onValidate: () -> Unit

) {
    if (addressInformation.isEmpty()) {
        onInvalidate(R.string.locationSearch)
        return
    }
    onValidate()
}