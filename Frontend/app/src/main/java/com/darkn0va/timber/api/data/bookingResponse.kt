package com.darkn0va.timber.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingResponse(
    @SerialName("Bookings")
    val bookings: List<Booking>?
)