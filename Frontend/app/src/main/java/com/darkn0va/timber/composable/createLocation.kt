package com.darkn0va.timber.composable

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkn0va.timber.R
import com.darkn0va.timber.api.*
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.data.User

@SuppressLint("StringFormatInvalid")
@Composable
fun CreateLocation(
    navController: NavController,
    user: User,
) {
    // LOWERCASE .lowercase(Locale.ROOT)

    val api = LocationAPI(ktorHttpClient)
    val client = ImageAPI(supabase)
    var validated by rememberSaveable { mutableStateOf(false) }
    var locationName by rememberSaveable { mutableStateOf("") }
    var nBeds by rememberSaveable { mutableIntStateOf(1) }
    val maxBeds = 16
    var isMaxBedsError by rememberSaveable { mutableStateOf(false) }
    var apartmentNumber by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var addressCity by rememberSaveable { mutableStateOf("") }
    var addressStreet by rememberSaveable { mutableStateOf("") }
    var addressState by rememberSaveable { mutableStateOf("") }
    var addressCountry by rememberSaveable { mutableStateOf("") }
    var img1URI by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> img1URI = uri }
    )

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Create location",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.padding(8.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.locationName) + "*",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
            value = locationName,
            onValueChange = { locationName = it },
            placeholder = { Text(text = "e.g. House in village") },
        )

        Spacer(modifier = Modifier.padding(2.dp))

        Button(
            modifier = Modifier.height(48.dp).align(Alignment.CenterHorizontally),
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text(
                text = "Select preview image"
            )
        }

        Spacer(modifier = Modifier.padding(2.dp))

        TextField(
            modifier = Modifier.width(198.dp),
            label = {
                Text(
                    text = stringResource(R.string.nBeds) + "*",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
            value = nBeds.toString(),
            onValueChange = {
                if (it.toInt() <= maxBeds) {
                    isMaxBedsError = false
                    nBeds = it.toInt()
                } else {
                    isMaxBedsError = true
                }
            },
            trailingIcon = {
                if (isMaxBedsError) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            placeholder = { Text(text = "e.g. 1") },
            isError = isMaxBedsError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (isMaxBedsError) {
            Text(
                text = "You cannot have more than 16 beds",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        TextField(
            value = apartmentNumber,
            label = {
                Text(
                    text = stringResource(R.string.apartmentNumber),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            onValueChange = {
                apartmentNumber = it
            },
            placeholder = { Text(text = "e.g. 4") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    modifier = Modifier.width(142.dp),
                    supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
                    label = {
                        Text(
                            text = stringResource(R.string.addressCity) + "*",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    value = addressCity,
                    onValueChange = {
                        addressCity = it
                    },
                    placeholder = { Text(text = "e.g. Karviná") },
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
                    value = addressStreet,
                    label = {
                        Text(
                            text = stringResource(R.string.addressStreet) + "*",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onValueChange = {
                        addressStreet = it
                    },
                    placeholder = { Text(text = "e.g. Rudé armády 2213") },
                )
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        TextField(
            modifier = Modifier.width(256.dp),
            label = {
                Text(
                    text = stringResource(R.string.addressState),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            value = addressState,
            onValueChange = {
                addressState = it
            },
            placeholder = { Text(text = "e.g. Moravskoslezský") },
        )

        Spacer(modifier = Modifier.padding(4.dp))

        TextField(
            modifier = Modifier.width(256.dp),
            supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
            value = addressCountry,
            label = {
                Text(
                    text = stringResource(R.string.addressCountry) + "*",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            onValueChange = {
                addressCountry = it
            },
            placeholder = { Text(text = "e.g. Czech republic") },
        )

        Spacer(modifier = Modifier.padding(4.dp))

        TextField(
            modifier = Modifier.width(256.dp),
            supportingText = { Text(text = "*required", style = MaterialTheme.typography.bodySmall) },
            label = {
                Text(
                    text = stringResource(R.string.description) + "*",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            value = description,
            onValueChange = { description = it },
            placeholder = { Text(text = "e.g. Near center") },
        )

        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                validateLocation(
                    name = locationName,
                    beds = nBeds,
                    addressCity = addressCity,
                    addressStreet = addressStreet,
                    addressCountry = addressCountry,
                    image1 = img1URI,
                    onInvalidate = { resourceId ->
                        val formattedString =
                            context.getString(R.string.value_is_empty) + " " + context.getString(resourceId)
                        Toast.makeText(
                            context,
                            formattedString,
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onValidate = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.succes),
                            Toast.LENGTH_LONG
                        ).show()
                        validated = true
                    }
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text(
                text = "Create",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    LaunchedEffect(validated) {
        if (validated) {
            val locationUUID = api.createLocation(
                Location(
                    null,
                    user.userUUID,
                    locationName.lowercase(),
                    nBeds,
                    description.lowercase(),
                    addressCity.lowercase(),
                    addressStreet.lowercase(),
                    apartmentNumber.lowercase(),
                    addressState.lowercase(),
                    addressCountry.lowercase(),
                    img1URI.toString().split("/").last(),
                    null, // image2
                    null, // image3
                    null, // image4
                    null, // image5
                    null, // image6
                    null, // image7
                    null, // image8
                    null, // image9
                    null, // image10
                    0.0f, // rating
                    true // isActive
                )
            )
            val contentResolver = context.contentResolver
            val inputStream = img1URI?.let { contentResolver.openInputStream(it) }

            client.saveImage(BitmapFactory.decodeStream(inputStream), locationUUID, img1URI.toString().split("/").last())

            api.updateLocationImage(locationUUID, img1URI.toString().split("/").last())
            navController.navigate("location/$locationUUID")
        }
    }
}

private fun validateLocation(
    name: String,
    beds: Int,
    addressCity: String,
    addressStreet: String,
    addressCountry: String,
    image1: Uri?,
//    image2: String?,
//    image3: String?,
//    image4: String?,
//    image5: String?,
//    image6: String?,
//    image7: String?,
//    image8: String?,
//    image9: String?,
//    image10: String?,
    onInvalidate: (Int) -> Unit,
    onValidate: () -> Unit

) {
    if (name.isEmpty()) {
        onInvalidate(R.string.locationName)
        return
    }

    if (beds == 0) {
        onInvalidate(R.string.nBeds)
        return
    }

    if (addressCity.isEmpty()) {
        onInvalidate(R.string.addressCity)
        return
    }

    if (addressStreet.isEmpty()) {
        onInvalidate(R.string.addressStreet)
        return
    }

    if (addressCountry.isEmpty()) {
        onInvalidate(R.string.addressCountry)
        return
    }

    if (image1 == null) {
        onInvalidate(R.string.img1)
        return
    }

    onValidate()
}