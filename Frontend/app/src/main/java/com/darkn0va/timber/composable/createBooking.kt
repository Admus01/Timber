package com.darkn0va.timber.composable

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkn0va.timber.R
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.data.Booking
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.api.ktorHttpClient
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StringFormatInvalid")
@Composable
fun CreateBooking(
    navController: NavController,
    user: User,
    locationUUID: String
) {
    // LOWERCASE .lowercase(Locale.ROOT)

    val api = LocationAPI(ktorHttpClient)
    var validated by rememberSaveable { mutableStateOf(false) }
    var locationName by rememberSaveable { mutableStateOf("") }
    var endDate by remember { mutableStateOf(LocalDate.parse("1921-10-01")) }
    var startDate by remember { mutableStateOf(LocalDate.parse("1921-10-01")) }
    var isPressedStart = remember { mutableStateOf(false) }
    var isPressedEnd = remember { mutableStateOf(false) }

    val currentDate = Clock.System.now()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Create booking",
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
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            isPressedStart.value = true
                        }
                    ) {
                        Text(startDate.toString())
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            isPressedEnd.value = true
                        }
                    ) {
                        Text(endDate.toString())
                    }
                }
            }
        }

        if (isPressedStart.value) {
            val datePickerState = rememberDatePickerState()
            val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

            DatePickerDialog(
                onDismissRequest = {
                    isPressedStart.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isPressedStart.value = false
                            var date: Instant? = null
                            if (datePickerState.selectedDateMillis != null) {
                                date = Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            }
                            startDate = date!!.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        }
                    ) {
                        Text("Okay")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isPressedStart.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        if (isPressedEnd.value) {
            val datePickerState = rememberDatePickerState()
            val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

            DatePickerDialog(
                onDismissRequest = {
                    isPressedEnd.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isPressedEnd.value = false
                            var date: Instant? = null
                            if (datePickerState.selectedDateMillis != null) {
                                date = Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            }
                            endDate = date!!.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        }
                    ) {
                        Text("Okay")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isPressedEnd.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }


        Spacer(modifier = Modifier.padding(2.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                validateLocation(
                    startDate,
                    endDate,
                    currentDate.toLocalDateTime(TimeZone.currentSystemDefault()).date,
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
                            context.getString(R.string.succesBooking),
                            Toast.LENGTH_LONG
                        ).show()
                        validated = true
                    }
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text(
                text = "Book",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    LaunchedEffect(validated) {
        if (validated) {
            val bookingResponse = api.createBooking(
                Booking(
                    locationUUID = locationUUID,
                    bookingUUID = null,
                    bookedUserUUID = user.userUUID!!,
                    bookedFrom = startDate,
                    bookedTill = endDate
                )
            )
            Log.d("BOO", bookingResponse)
            navController.navigate("bookings")
        }
    }
}

private fun validateLocation(
    startDate: LocalDate,
    endDate: LocalDate,
    currentDate: LocalDate,
    onInvalidate: (Int) -> Unit,
    onValidate: () -> Unit

) {
    if (startDate == currentDate) {
        onInvalidate(R.string.bookingStartDate)
        return
    }
    if (endDate == currentDate || endDate == startDate) {
        onInvalidate(R.string.bookingEndDate)
        return
    }

    onValidate()
}