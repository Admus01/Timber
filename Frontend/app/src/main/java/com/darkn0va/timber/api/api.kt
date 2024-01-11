package com.darkn0va.timber.api

import android.util.Log
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.data.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

private const val TIMEOUT = 60_000L

@OptIn(ExperimentalSerializationApi::class)
val ktorHttpClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            explicitNulls = false
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT
        connectTimeoutMillis = TIMEOUT
        socketTimeoutMillis = TIMEOUT
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("Ktor =>", message)
            }
        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            Log.d("HTTP status:", "${response.status.value}")
        }
    }
    defaultRequest {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

suspend fun testAPI(client: HttpClient): String {
    return client.get {
        url {
            host = "172.20.110.230"
            port = 8089
        }
    }.body<String>().toString()
}

class LocationAPI(private val client: HttpClient) {
    suspend fun getLocationSearch(pageIndex: Number, addressInformation: String): List<Location> {
        return client.post {
            url {
                host = Location.URL
                port = Location.PORT
                path("location")
            }
            setBody(
                """
                {
                    "page_index": $pageIndex,
                    "address_information": "${addressInformation.lowercase()}"
                }
                """.trimIndent()
            )
        }.body()
    }
}

class userAPI(private val client: HttpClient) {

    suspend fun perform_handshake(idToken: String): User {
        validate_client(idToken)

        return User("tmmnhlxzpj1eightldxhjnone97", "Filip", "Valentiny", "valentinyfilip@protonmail.cz", "", "+420", 737015152, Date(25072004), "cz")
    }
    suspend fun validate_client(idToken: String): HttpResponse {
        return client.get {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("validate_client", idToken)
            }
        }.body()
    }

    suspend fun performHandshakeDebug(idToken: String, user: User): User {
        val validated_idToken = validateClientDebug(idToken).removeSurrounding("\"")
        val loginResponse = loginUser(validated_idToken)

        val userLogged: User = if (loginResponse.status == HttpStatusCode.OK) {
            val userUUID = loginResponse.body<UserUUID>()
            Log.d("LOG", userUUID.toString())
            getUserData(userUUID)
        } else {
            val userUUID = registerUser(validated_idToken, user).body<UserUUID>()
            Log.d("LOG", userUUID.toString())
            getUserData(userUUID)
        }

        return userLogged
    }
    private suspend fun validateClientDebug(idToken: String): String {
        return client.get {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("validate_client_debug", idToken)
            }
        }.body()
    }

    suspend fun registerUser(idToken: String, user: User): HttpResponse {
        val newUser = User(null, user.firstName, user.lastName, user.email, idToken, user.countryPhoneCode, user.phoneNumber, user.dateOfBirth, user.citizenShip)
        return client.post {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("register")
            }
            setBody(
                newUser
            )
        }
    }

    suspend fun loginUser(idToken: String): HttpResponse {
        return client.post {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("login")
            }
            headers {
                append(HttpHeaders.Authorization, idToken)
            }
        }
    }

    suspend fun getUserData(userUUID: UserUUID): User {
        return client.get {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("user", userUUID.userUUID)
            }
        }.body()
    }
}

@Serializable
data class UserUUID(
    @SerialName("user_uuid")
    val userUUID: String
)