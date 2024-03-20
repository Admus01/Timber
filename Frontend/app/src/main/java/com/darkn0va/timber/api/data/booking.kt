package com.darkn0va.timber.api.data

import com.darkn0va.timber.api.data.serilizators.LocalDateIso8601Serializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    @SerialName("location_uuid")
    val locationUUID: String,
    @SerialName("booking_uuid")
    val bookingUUID: String?,
    @SerialName("booked_user_uuid")
    val bookedUserUUID: String,
    @SerialName("booked_from")
    @Serializable(with = LocalDateIso8601Serializer::class)
    val bookedFrom: LocalDate?,
    @SerialName("booked_till")
    @Serializable(with = LocalDateIso8601Serializer::class)
    val bookedTill: LocalDate?,
) {
    constructor() : this(
        "",
        "",
        "",
        null,
        null
    )

    companion object {
        const val URL = "172.20.110.230"
        const val PORT = 8000
    }
}