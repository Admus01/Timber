package com.darkn0va.timber.api.data

import com.darkn0va.timber.api.data.serilizators.LocalDateIso8601Serializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_uuid")
    val userUUID: String?,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("email")
    val email: String,
    @SerialName("id_token")
    val idToken: String?,
    @SerialName("country_phone_code")
    val countryPhoneCode: String,
    @SerialName("phone_number")
    val phoneNumber: Int,
    @SerialName("date_of_birth")
    @Serializable(with = LocalDateIso8601Serializer::class)
    val dateOfBirth: LocalDate?,
    @SerialName("citizenship")
    val citizenShip: String,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        null,
        "",
        0,
        null,
        "",
    )

    companion object {
        const val URL = "172.20.110.230"
        const val PORT = 8000
    }
}