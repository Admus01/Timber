package com.darkn0va.timber.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingUUID(
    @SerialName("booking_uuid")
    val userUUID: String
)