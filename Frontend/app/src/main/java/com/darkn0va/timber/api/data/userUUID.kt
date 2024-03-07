package com.darkn0va.timber.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUUID(
    @SerialName("user_uuid")
    val userUUID: String
)