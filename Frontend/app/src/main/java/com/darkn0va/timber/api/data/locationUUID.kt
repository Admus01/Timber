package com.darkn0va.timber.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationUUID(
    @SerialName("location_uuid")
    val locationUUID: String
)