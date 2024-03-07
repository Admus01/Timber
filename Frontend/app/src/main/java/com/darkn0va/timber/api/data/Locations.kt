package com.darkn0va.timber.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("location_uuid")
    val locationUUID: String?,
    @SerialName("user_uuid")
    val userUUID: String?,
    @SerialName("name")
    val name: String,
    @SerialName("beds")
    val beds: Int,
    @SerialName("description")
    val description: String,
    @SerialName("address_city")
    val addressCity: String,
    @SerialName("address_street")
    val addressStreet: String,
    @SerialName("address_apartment_number")
    val addressApartmentNumber: String?,
    @SerialName("address_state")
    val addressState: String?,
    @SerialName("address_country")
    val addressCountry: String,
    val image1: String,
    val image2: String? = null,
    val image3: String? = null,
    val image4: String? = null,
    val image5: String? = null,
    val image6: String? = null,
    val image7: String? = null,
    val image8: String? = null,
    val image9: String? = null,
    val image10: String? = null,
    @SerialName("rating")
    val rating: Float,
    @SerialName("is_active")
    val isActive: Boolean,
) {
    constructor() : this(
        locationUUID = null,
        "",
        "",
        0,
        "",
        "",
        "",
        null,
        null,
        "",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0F,
        true,
//        null
    )

    companion object {
        const val URL = "172.20.110.230"
        const val PORT = 8000
    }
}